package com.github.ilooner.polygoncli.client;

import com.github.ilooner.polygoncli.client.model.json.StockAggregateListJSON;
import com.github.ilooner.polygoncli.client.model.json.StockTradesListJSON;
import com.github.ilooner.polygoncli.config.ConfigLoader;
import org.junit.Assert;
import org.junit.Ignore;
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

    @Test
    public void getAggregatesTest() throws IOException, ExecutionException, InterruptedException {
        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final PolygonAPI api = new PolygonController(config).build();

        final StockAggregateListJSON aggregateListJSON = api.getStockAggregates(
                "AAPL",
                1,
                "minute",
                "2020-10-14",
                "2020-10-14",
                false,
                "asc",
                50000).get();

        Assert.assertFalse(aggregateListJSON.getResults().isEmpty());
    }

    @Ignore
    @Test
    public void getStockTradesTest() throws IOException, ExecutionException, InterruptedException {
        final var config = new ConfigLoader().load(ConfigLoader.DEFAULT_CONFIG);
        final PolygonAPI api = new PolygonController(config).build();

        final StockTradesListJSON tradesListJSON = api.getStockTrades(
                "AAPL",
                "2020-10-14",
                null,
                null,
                false,
                100).get();

        System.out.println(tradesListJSON);
    }
}
