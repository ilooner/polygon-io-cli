package com.github.ilooner.polygoncli.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ilooner.polygoncli.client.model.Aggregate;
import com.github.ilooner.polygoncli.client.model.Trade;
import com.github.ilooner.polygoncli.config.ConfigLoader;
import com.github.ilooner.polygoncli.output.MemoryOutputter;
import com.github.ilooner.polygoncli.utils.DateUtils;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class PolygonClientTest {
    @Test
    public void getStockExchangesTest() throws Exception {
        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final var client = new PolygonClient(config);

        Assert.assertFalse(client.getStockExchanges().isEmpty());
    }

    @Test
    public void singleQueryAggregatesTest() throws Exception {
        final LocalDate startDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-09-01");
        final LocalDate endDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-09-10");
        final Set<LocalDate> expectedDates = createWeekdayDateSetInclusive(
                startDate, endDate);

        expectedDates.remove(PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-09-07"));

        getAggregatesHelper("AAPL", startDate, endDate, expectedDates);
    }

    @Test
    public void multiQueryAggregatesTest() throws Exception {
        final LocalDate startDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2019-01-10");
        final LocalDate endDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2019-09-10");
        final Set<LocalDate> expectedDates = createWeekdayDateSetInclusive(
                startDate, endDate);

        final Set<LocalDate> holidays = Sets.newHashSet(
                "2019-09-02",
                "2019-04-19",
                "2019-02-18",
                "2019-01-21",
                "2019-05-27",
                "2019-07-04")
                .stream()
                .map(dateString -> PolygonClient.DATE_TIME_FORMATTER.parseLocalDate(dateString))
                .collect(Collectors.toSet());

        expectedDates.removeAll(holidays);

        getAggregatesHelper("AAPL", startDate, endDate, expectedDates);
    }

    @Test
    public void queryStockTradesWeekdaysOnly() throws Exception {
        final LocalDate startDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-01-06");
        final LocalDate endDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-01-07");

        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final var client = new PolygonClient(config);
        final var outputter = new MemoryOutputter<Trade>();

        final var expectedDates = Sets.newHashSet(startDate, endDate);
        final var actualDates = new HashSet<>();

        client.outputStockTrades("KO", startDate, endDate, outputter);

        for (Trade trade: outputter.getOutputList()) {
            final var millis = trade.getTimestampNano() / 1_000_000L;
            actualDates.add(new DateTime(millis).toLocalDate());
        }

        Assert.assertTrue(outputter.getOutputList().size() > 100_000);
        Assert.assertEquals(expectedDates, actualDates);
    }

    @Test
    public void queryStockTradesWeekendOnly() throws Exception {
        final LocalDate startDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-01-11");
        final LocalDate endDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-01-12");

        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final var client = new PolygonClient(config);
        final var outputter = new MemoryOutputter<Trade>();

        client.outputStockTrades("KO", startDate, endDate, outputter);

        Assert.assertTrue(outputter.getOutputList().isEmpty());
    }

    @Test
    public void queryStockTradesStartWeekend() throws Exception {
        final LocalDate startDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-01-12");
        final LocalDate endDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-01-13");

        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final var client = new PolygonClient(config);
        final var outputter = new MemoryOutputter<Trade>();

        client.outputStockTrades("KO", startDate, endDate, outputter);

        final var expectedDates = Sets.newHashSet(endDate);
        final var actualDates = new HashSet<>();

        for (Trade trade: outputter.getOutputList()) {
            final var millis = trade.getTimestampNano() / 1_000_000L;
            final var localDates = new DateTime(millis).toLocalDate();
            actualDates.add(localDates);
        }

        Assert.assertTrue(outputter.getOutputList().size() > 50_000);
        Assert.assertEquals(expectedDates, actualDates);
    }

    @Test
    public void queryStockTradesEndWeekend() throws Exception {
        final LocalDate startDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-01-10");
        final LocalDate endDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-01-11");

        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final var client = new PolygonClient(config);
        final var outputter = new MemoryOutputter<Trade>();

        client.outputStockTrades("KO", startDate, endDate, outputter);

        final var expectedDates = Sets.newHashSet(startDate);
        final var actualDates = new HashSet<>();

        for (Trade trade: outputter.getOutputList()) {
            final var millis = trade.getTimestampNano() / 1_000_000L;
            final var localDates = new DateTime(millis).toLocalDate();
            actualDates.add(localDates);
        }

        Assert.assertTrue(outputter.getOutputList().size() > 50_000);
        Assert.assertEquals(expectedDates, actualDates);
    }

    @JsonIgnore
    @Test
    public void realWorldStockTradesQuery() throws Exception {
        final LocalDate startDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-12-20");
        final LocalDate endDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-12-25");

        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final var client = new PolygonClient(config);
        final var outputter = new MemoryOutputter<Trade>();

        final var expectedDates = Sets.newHashSet(
                "2020-12-21",
                "2020-12-22",
                "2020-12-23",
                "2020-12-24")
                .stream()
                .map(dateString -> PolygonClient.DATE_TIME_FORMATTER.parseLocalDate(dateString))
                .collect(Collectors.toSet());
        final var actualDates = new HashSet<>();

        client.outputStockTrades("KO", startDate, endDate, outputter);

        for (Trade trade: outputter.getOutputList()) {
            final var millis = trade.getTimestampNano() / 1_000_000L;
            final var localDates = new DateTime(millis).toLocalDate();
            actualDates.add(localDates);
        }

        Assert.assertEquals(expectedDates, actualDates);
    }

    @Test
    public void realWorldStockAggregatesQuery() throws Exception {
        final LocalDate startDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-12-20");
        final LocalDate endDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-12-25");

        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final var client = new PolygonClient(config);
        final var outputter = new MemoryOutputter<Aggregate>();

        client.outputStockAggregates("KO", startDate, endDate, outputter);
        // polygon stocks aggregates -t KO -s 2020-12-20 -e 2020-12-25 csv -o ~/testKOAggregates.csv -h
    }

    private Set<LocalDate> createWeekdayDateSetInclusive(final LocalDate startDate,
                                                         final LocalDate endDate) {
        final var dates = new HashSet<LocalDate>();

        for (LocalDate current = startDate;
             current.isBefore(endDate) || current.isEqual(endDate);
             current = current.plusDays(1)) {

            if (DateUtils.isWeekend(current.toDateTimeAtCurrentTime())) {
                continue;
            }

            dates.add(current);
        }

        return dates;
    }

    private void getAggregatesHelper(final String ticker,
                                     final LocalDate startDate,
                                     final LocalDate endDate,
                                     final Set<LocalDate> expectedDates) throws Exception {
        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final var client = new PolygonClient(config);
        final var outputter = new MemoryOutputter<Aggregate>();

        client.outputStockAggregates(ticker, startDate, endDate, outputter);

        final Set<LocalDate> actualDates = new HashSet<>();
        final Map<LocalDate, AtomicLong> counts = new HashMap<>();

        for (Aggregate aggregate: outputter.getOutputList()) {
            final var dateTime = new DateTime(aggregate.getTimestamp());
            final var localDate = dateTime.toLocalDate();

            actualDates.add(localDate);

            AtomicLong atomicLong = counts.get(localDate);

            if (atomicLong == null) {
                atomicLong = new AtomicLong(0);
                counts.put(localDate, atomicLong);
            }

            atomicLong.incrementAndGet();
        }

        for (Map.Entry<LocalDate, AtomicLong> entry: counts.entrySet()) {
            final String msg = String.format("%d aggregates", entry.getValue().get());
            Assert.assertTrue(msg, entry.getValue().get() > 300L);
        }

        final var diff = new HashSet<>(expectedDates);
        diff.removeAll(actualDates);

        final String msg = String.format("expected size %d actual size %d, %s",
                expectedDates.size(), actualDates.size(), diff.toString());

        Assert.assertEquals(msg, expectedDates, actualDates);
    }
}
