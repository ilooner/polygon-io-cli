package com.github.ilooner.polygoncli.client;

import com.github.ilooner.polygoncli.config.ConfigLoader;
import org.junit.Assert;
import org.junit.Test;

public class PolygonClientTest {
    @Test
    public void getStockExchangesTest() throws Exception {
        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final var client = new PolygonClient(config);

        Assert.assertFalse(client.getStockExchanges().isEmpty());
    }
}