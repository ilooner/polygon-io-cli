package com.github.ilooner.polygoncli.output;

import com.github.ilooner.polygoncli.client.model.Aggregate;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.attribute.PosixFilePermissions.asFileAttribute;
import static java.nio.file.attribute.PosixFilePermissions.fromString;

public class CSVOutputterTest {
    @Test
    public void writeWithoutHeader() throws Exception {
        final Path file = Files.createTempFile("testcsv",
                "json",
                asFileAttribute(fromString("rw-------")));
        file.toFile().deleteOnExit();

        final var outputter = new CSVOutputter.Builder()
                .build(Aggregate.getClassSchema(), file);
        final var records = Lists.newArrayList(
                createAgg(3L, 100L, 10L, 13.0, 14.0, 15.0, 16.0, 17.0),
                createAgg(4L, 101L, 11L, 14.0, 15.0, 16.0, 17.0, 18.0),
                createAgg(5L, 102L, 12L, 15.0, 16.0, 17.0, 18.0, 19.0));

        outputter.output((List) records);
        outputter.finish();

        final var expected = Lists.newArrayList(
                "17.0,13.0,15.0,14.0,16.0,10,100,3",
                "18.0,14.0,16.0,15.0,17.0,11,101,4",
                "19.0,15.0,17.0,16.0,18.0,12,102,5");
        final var actual = Files.readAllLines(file);

        Assert.assertEquals(expected, actual);
    }

    private Aggregate createAgg(final long numberOfItems,
                                final long timestamp,
                                final long volume,
                                final double close,
                                final double lowest,
                                final double highest,
                                final double average,
                                final double open) {
        final var agg = new Aggregate();
        agg.setNumberOfItems(numberOfItems);
        agg.setTimestamp(timestamp);
        agg.setVolume(volume);
        agg.setClosePrice(close);
        agg.setLowestPrice(lowest);
        agg.setHighestPrice(highest);
        agg.setAverage(average);
        agg.setOpenPrice(open);
        return agg;
    }
}
