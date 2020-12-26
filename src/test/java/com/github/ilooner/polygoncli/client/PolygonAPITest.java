package com.github.ilooner.polygoncli.client;

import com.github.ilooner.polygoncli.config.ConfigLoader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class PolygonAPITest {
    @Test
    public void getStockExchangesTest() throws IOException, ExecutionException, InterruptedException {
        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final PolygonAPI api = new PolygonController(config).build();

        Assert.assertFalse(api.getStockExchanges().get().isEmpty());
    }
}
