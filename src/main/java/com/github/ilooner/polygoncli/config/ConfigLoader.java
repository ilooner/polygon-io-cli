package com.github.ilooner.polygoncli.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    public static final Path DEFAULT_CONFIG = Path.of(URI.create(String.format(
            "file://%s/.polygon/config.json", System.getProperty("user.home"))));
    private static final ObjectMapper OM = new ObjectMapper();

    public PolygonConfig load(Path configPath) throws IOException {
        final byte[] bytes = Files.readAllBytes(configPath);
        return OM.readValue(bytes, PolygonConfig.class);
    }
}



