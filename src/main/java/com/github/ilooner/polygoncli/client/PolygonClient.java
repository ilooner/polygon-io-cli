package com.github.ilooner.polygoncli.client;

import com.github.ilooner.polygoncli.client.model.Aggregate;
import com.github.ilooner.polygoncli.client.model.Trade;
import com.github.ilooner.polygoncli.client.model.json.*;
import com.github.ilooner.polygoncli.config.PolygonConfig;
import com.github.ilooner.polygoncli.output.Outputter;
import com.github.ilooner.polygoncli.utils.DateUtils;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.apache.avro.specific.SpecificRecord;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import retrofit2.HttpException;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PolygonClient {
    public static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(15L);
    public static final String AGGREGATES_SORT_ASC = "asc";
    public static final String AGGREGATES_TIMESPAN = "minute";
    public static final int LIMIT = 50000;
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
                .retryExceptions(IOException.class, TimeoutException.class)
                .retryOnException(new RetryPredicate())
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
                endDate.toDateTimeAtStartOfDay().plusHours(23),
                outputter,
                (ticker1, startDate1, endDate1) -> getStockAggregates(ticker1, startDate1, endDate1));
    }

    public void outputStockTrades(final String ticker,
                                  final LocalDate startDate,
                                  final LocalDate endDate,
                                  final Outputter<Trade> outputter) throws Exception {
        outputDataPoints(ticker,
                startDate.toDateTimeAtStartOfDay(),
                endDate.toDateTimeAtStartOfDay().plusHours(23),
                outputter,
                (ticker1, startDate1, endDate1) -> getStockTrades(ticker1, startDate1));
    }

    protected <A extends SpecificRecord, T extends JSONData<A>> void outputDataPoints(
                                    final String ticker,
                                    final DateTime startDate,
                                    final DateTime endDate,
                                    final Outputter<A> outputter,
                                    final DataSupplier<A, T> supplier) throws Exception {
        final MutableObject<Long> lastTimestamp = new MutableObject<>();
        final MutableObject<Long> lastSeqNo = new MutableObject<>();
        final MutableObject<DateTime> computedStartDate = new MutableObject<>(startDate);

        while (true) {
            final var tmpLT = lastTimestamp.getValue();
            final var tmpSN = lastSeqNo.getValue();
            final List<T> jsonDataList;

            try {
                jsonDataList = supplier.get(ticker, computedStartDate.getValue(), endDate);
            } catch (Exception ex) {
                if (RetryPredicate.noDataExists(ex)) {
                    // No date exists for this date, so we need to proceed to next date
                    if (next(lastTimestamp, lastSeqNo, computedStartDate, endDate)) {
                        // We are not done yet. We need to try another data fetch.
                        continue;
                    } else {
                        // We are done, there is no more data for us to get
                        break;
                    }
                } else {
                    throw ex;
                }
            }

            if (jsonDataList == null) {
                if (next(lastTimestamp, lastSeqNo, computedStartDate, endDate)) {
                    // We are not done yet. We need to try another data fetch.
                    continue;
                } else {
                    // We are done, there is no more data for us to get
                    break;
                }
            }

            final var convertedList = jsonDataList
                    .stream()
                    .filter(data -> tmpLT == null || data.getTimestampMillis() >= tmpLT)
                    .filter(data -> data.getTimestampMillis() <= endDate.getMillis())
                    .filter(data -> tmpSN == null || data.getSeqNo() > tmpSN)
                    .map(data -> data.convert())
                    .collect(Collectors.toList());

            outputter.output(convertedList);

            final var size = jsonDataList.size();
            final var last = jsonDataList.get(size - 1);

            lastTimestamp.setValue(last.getTimestampMillis());
            lastSeqNo.setValue(last.getSeqNo());
            var nextStartDate = new DateTime(lastTimestamp.getValue());

            if (size < LIMIT) {
                if (nextStartDate.toLocalDate().equals(endDate.toLocalDate())) {
                    break;
                } else {
                    nextStartDate = DateUtils.nextWeekDay(nextStartDate);
                }
            }

            if (!computedStartDate.getValue().toLocalDate().equals(nextStartDate.toLocalDate())) {
                // Sequence numbers reset for new days
                lastSeqNo.setValue(null);
            }

            if (nextStartDate.toLocalDate().isAfter(endDate.toLocalDate())) {
                // We are done, there is no more data for us to get
                break;
            }

            computedStartDate.setValue(nextStartDate);
            lastTimestamp.setValue(computedStartDate.getValue().getMillis());
        }

        outputter.finish();
    }

    /**
     * @return True to continue. False to stop.
     */
    private boolean next(final MutableObject<Long> lastTimestamp,
                         final MutableObject<Long> lastSeqNo,
                         final MutableObject<DateTime> computedStartDate,
                         final DateTime endDate) {
        computedStartDate.setValue(DateUtils.nextWeekDay(computedStartDate.getValue()));
        lastTimestamp.setValue(computedStartDate.getValue().getMillis());;
        lastSeqNo.setValue(null);

        return !computedStartDate.getValue().toLocalDate().isAfter(endDate.toLocalDate());
    }

    protected List<StockTradeJSON> getStockTrades(final String ticker,
                                                  final DateTime startDate) throws Exception {
        return execute(() -> polygonAPI.getStockTrades(
                ticker,
                startDate.toString(DATE_TIME_FORMATTER),
                "asc",
                "timestamp",
                LIMIT).get(REQUEST_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS))
                .getResults();
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
                LIMIT).get(REQUEST_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS))
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

    public static class RetryPredicate implements Predicate<Throwable> {
        @Override
        public boolean test(Throwable throwable) {
            return !noDataExists(throwable);
        }

        public static boolean noDataExists(Throwable throwable) {
            if (throwable instanceof ExecutionException) {
                final var cause = throwable.getCause();

                if (cause instanceof HttpException) {
                    return noDataExists((HttpException) cause);
                } else {
                    return false;
                }
            } else if (throwable instanceof HttpException) {
                return noDataExists((HttpException) throwable);
            } else {
                return false;
            }
        }

        public static boolean noDataExists(HttpException ex) {
            return ex.code() == 404;
        }
    }
}
