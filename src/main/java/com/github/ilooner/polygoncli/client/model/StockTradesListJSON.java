package com.github.ilooner.polygoncli.client.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StockTradesListJSON {
    private String ticker;
    private int results_count;
    private long db_latency;
    private boolean success;
    private List<StockTradeJSON> results;
}
