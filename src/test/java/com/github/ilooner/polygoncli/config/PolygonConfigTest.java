package com.github.ilooner.polygoncli.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.nio.file.attribute.PosixFilePermissions.asFileAttribute;
import static java.nio.file.attribute.PosixFilePermissions.fromString;

public class PolygonConfigTest {
    @Test
    public void loadConfigTest() throws IOException {
        final Path file = Files.createTempFile("testconfig",
                "json",
                asFileAttribute(fromString("rw-------")));
        file.toFile().deleteOnExit();

        final ObjectMapper om = new ObjectMapper();
        final PolygonConfig expected = new PolygonConfig("blahblah");
        final byte[] bytes = om.writeValueAsBytes(expected);

        Files.write(file, bytes, StandardOpenOption.APPEND);

        final ConfigLoader configLoader = new ConfigLoader();
        final PolygonConfig actual = configLoader.load(file);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDefaultConfig() throws IOException {
        Assert.assertNotNull(new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG).getApiKey());
    }
}