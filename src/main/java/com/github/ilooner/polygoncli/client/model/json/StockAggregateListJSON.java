package com.github.ilooner.polygoncli.client.model.json;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StockAggregateListJSON {
    private String ticker;
    private String status;
    private int queryCount;
    private int resultsCount;
    private boolean adjusted;
    private String request_id;
    private List<StockAggregateJSON> results;
}
