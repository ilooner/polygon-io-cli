package com.github.ilooner.polygoncli.client;

import com.github.ilooner.polygoncli.client.model.Aggregate;
import com.github.ilooner.polygoncli.config.ConfigLoader;
import com.github.ilooner.polygoncli.output.MemoryOutputter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class PolygonClientTest {
    @Test
    public void getStockExchangesTest() throws Exception {
        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final var client = new PolygonClient(config);

        Assert.assertFalse(client.getStockExchanges().isEmpty());
    }

    @Test
    public void getAggregatesTest() throws Exception {
        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final var client = new PolygonClient(config);
        final var outputter = new MemoryOutputter<Aggregate>();

        final LocalDate startDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-09-01");
        final LocalDate endDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate("2020-09-10");

        client.outputStockAggregates("AAPL", startDate, endDate, outputter);

        final Set<LocalDate> expectedDates = new HashSet<>();
        final Set<LocalDate> actualDates = new HashSet<>();
        final Map<LocalDate, AtomicLong> counts = new HashMap<>();

        for (int i = 1; i <= 10; i++) {
            final var localDateString = String.format("2020-09-%02d", i);
            final var localDate = PolygonClient.DATE_TIME_FORMATTER.parseLocalDate(localDateString);

            if (localDate.getDayOfWeek() == DateTimeConstants.SATURDAY ||
                localDate.getDayOfWeek() == DateTimeConstants.SUNDAY ||
                localDate.getDayOfMonth() == 7) {
                continue;
            }

            expectedDates.add(PolygonClient.DATE_TIME_FORMATTER.parseLocalDate(localDateString));
        }

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
            Assert.assertTrue(entry.getValue().get() > 720L);
        }

        Assert.assertEquals(expectedDates, actualDates);
    }
}
