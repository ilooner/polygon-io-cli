package com.github.ilooner.polygoncli.client;

import com.github.ilooner.polygoncli.client.model.StockExchangeJSON;
import retrofit2.http.GET;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PolygonAPI {
    @GET("/v1/meta/exchanges")
    CompletableFuture<List<StockExchangeJSON>> getStockExchanges();
    // CompletableFuture<StockTradesListJSON> getStockTrades();
}
