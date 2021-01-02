package com.github.ilooner.polygoncli.client;

import com.github.ilooner.polygoncli.client.model.Aggregate;
import com.github.ilooner.polygoncli.client.model.json.*;
import com.github.ilooner.polygoncli.config.PolygonConfig;
import com.github.ilooner.polygoncli.output.Outputter;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.apache.avro.specific.SpecificRecord;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import retrofit2.HttpException;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PolygonClient {
    public static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(15L);
    public static final String AGGREGATES_SORT_ASC = "asc";
    public static final String AGGREGATES_TIMESPAN = "minute";
    public static final int AGGREGATES_LIMIT = 50000;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

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
                .waitDuration(Duration.ofSeconds(30L))
                .retryOnResult(result -> result == null)
                .retryExceptions(IOException.class, TimeoutException.class, ExecutionException.class, HttpException.class)
                .ignoreExceptions(InterruptedException.class, CancellationException.class)
                .build();
        final var retryRegistry = RetryRegistry.custom().build();

        this.retry = retryRegistry.retry("polygon", retryConfig);
        this.polygonAPI = new PolygonController(config).build();
    }

    public List<StockExchangeJSON> getStockExchanges() throws Exception {
        return execute(() -> polygonAPI.getStockExchanges().get(REQUEST_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
    }

    public void outputStockAggregates(final String ticker,
                                      final LocalDate startDate,
                                      final LocalDate endDate,
                                      final Outputter<Aggregate> outputter) throws Exception {
        outputDataPoints(ticker,
                startDate.toDateTimeAtStartOfDay(),
                endDate.toDateTimeAtStartOfDay(),
                outputter,
                (ticker1, startDate1, endDate1) -> getStockAggregates(ticker1, startDate1, endDate1));
    }

    protected <A extends SpecificRecord, T extends JSONData<A>> void outputDataPoints(
                                    final String ticker,
                                    final DateTime startDate,
                                    final DateTime endDate,
                                    final Outputter<A> outputter,
                                    final DataSupplier<A, T> supplier) throws Exception {
        Long lastTimestamp = null;

        while (true) {
            final DateTime computedStartDate;

            if (lastTimestamp == null) {
                computedStartDate = startDate;
            } else {
                computedStartDate = new DateTime(lastTimestamp);
            }

            final var tmpLastTimestamp = lastTimestamp;
            final var jsonDataList = supplier.get(ticker, computedStartDate, endDate);
            final var convertedList = jsonDataList
                    .stream()
                    .filter(data -> tmpLastTimestamp == null || (data.getTimestampMillis() > tmpLastTimestamp))
                    .map(data -> data.convert())
                    .collect(Collectors.toList());

            outputter.output(convertedList);

            final var size = jsonDataList.size();
            final boolean done = size < AGGREGATES_LIMIT;

            if (done) {
                break;
            } else {
                lastTimestamp = jsonDataList.get(size - 1).getTimestampMillis();
            }
        }

        outputter.finish();
    }

    private List<StockTradeJSON> getStockTrades(final String ticker,
                                                final DateTime startDate,
                                                final DateTime endDate) throws Exception {
        return null;
    }

    private List<StockAggregateJSON> getStockAggregates(final String ticker,
                                                        final DateTime startDate,
                                                        final DateTime endDate) throws Exception {

        return execute(() -> polygonAPI.getStockAggregates(
                ticker,
                1,
                AGGREGATES_TIMESPAN,
                startDate.toString(DATE_TIME_FORMATTER),
                endDate.toString(DATE_TIME_FORMATTER),
                false,
                AGGREGATES_SORT_ASC,
                AGGREGATES_LIMIT).get(REQUEST_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS))
                .getResults();
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

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    public interface DataSupplier<A extends SpecificRecord, T extends JSONData<A>> {
        List<T> get(final String ticker, final DateTime startDate, final DateTime endDate) throws Exception;
    }
}
