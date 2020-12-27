package com.github.ilooner.polygoncli.client;

import com.github.ilooner.polygoncli.client.model.StockExchangeJSON;
import com.github.ilooner.polygoncli.config.PolygonConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

public class PolygonClient {
    public static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(15L);

    private final RateLimiter rateLimiter;
    private final Retry retry;
    private final PolygonAPI polygonAPI;

    public PolygonClient(final PolygonConfig config) {
        if (config.isLimited()) {
            final var rateLimiterConfig = RateLimiterConfig.custom()
                    .limitRefreshPeriod(Duration.ofMinutes(1))
                    .limitForPeriod(5)
                    .timeoutDuration(Duration.ofMinutes(1))
                    .build();
            final var rateLimiterRegistry = RateLimiterRegistry.of(rateLimiterConfig);

            this.rateLimiter = rateLimiterRegistry.rateLimiter("polygon");
        } else {
            this.rateLimiter = null;
        }

        final var retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(5L))
                .retryOnResult(result -> result == null)
                .retryExceptions(IOException.class, TimeoutException.class, ExecutionException.class)
                .ignoreExceptions(InterruptedException.class, CancellationException.class)
                .build();
        final var retryRegistry = RetryRegistry.custom().build();

        this.retry = retryRegistry.retry("polygon", retryConfig);
        this.polygonAPI = new PolygonController(config).build();
    }

    public List<StockExchangeJSON> getStockExchanges() throws Exception {
        return execute(() -> polygonAPI.getStockExchanges().get(REQUEST_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
    }

    private <T> T execute(final SupplierWithException<T> request) throws Exception {
        try {
            return Retry.decorateCheckedSupplier(retry, () -> {
                if (rateLimiter != null) {
                    rateLimiter.acquirePermission();
                }

                return request.get();
            }).apply();
        } catch (Exception ex) {
            throw ex;
        } catch (Throwable throwable) {
            throw ((Error) throwable);
        }
    }

    private <T> T execute(final SupplierWithException<T> request, CompletableFuture<T> first) throws Exception {
        try {
            return first.get(REQUEST_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (CancellationException | ExecutionException | TimeoutException | InterruptedException e) {
            // Swallow for retry
        }

        return execute(request);
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
