package com.github.ilooner.polygoncli.client;

import com.github.ilooner.polygoncli.client.model.StockAggregateListJSON;
import com.github.ilooner.polygoncli.client.model.StockExchangeJSON;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PolygonAPI {
    @GET("/v1/meta/exchanges")
    CompletableFuture<List<StockExchangeJSON>> getStockExchanges();

    @GET("/v2/aggs/ticker/{ticker}/range/{multiplier}/{timespan}/{from}/{to}")
    CompletableFuture<StockAggregateListJSON> getStockAggregates(@Path("ticker") String ticker,
                                                                 @Path("multiplier") int multiplier,
                                                                 @Path("timespan") String timespan,
                                                                 @Path("from") String from,
                                                                 @Path("to") String to,
                                                                 @Query("unadjusted") boolean unadjusted,
                                                                 @Query("sort") String sort,
                                                                 @Query("limit") int limit);
}
