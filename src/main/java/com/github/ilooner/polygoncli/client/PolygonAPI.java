package com.github.ilooner.polygoncli.client;

import com.github.ilooner.polygoncli.client.model.json.StockAggregateListJSON;
import com.github.ilooner.polygoncli.client.model.json.StockExchangeJSON;
import com.github.ilooner.polygoncli.client.model.json.StockTradesListJSON;
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
                                                                 @Query("adjusted") Boolean adjusted,
                                                                 @Query("sort") String sort,
                                                                 @Query("limit") Integer limit);

    @GET("/v3/trades/{ticker}")
    CompletableFuture<StockTradesListJSON> getStockTrades(@Path("ticker") String ticker,
                                                          @Query("timestamp") String timestamp,
                                                          @Query("order") String order,
                                                          @Query("sort") String sort,
                                                          @Query("limit") Integer limit);
}
