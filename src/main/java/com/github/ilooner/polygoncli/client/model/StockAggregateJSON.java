package com.github.ilooner.polygoncli.client.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StockAggregateJSON {
    private double o;
    private double h;
    private double l;
    private double c;
    private long v;
    private double vw;
    private long t;
    private long n;
}
