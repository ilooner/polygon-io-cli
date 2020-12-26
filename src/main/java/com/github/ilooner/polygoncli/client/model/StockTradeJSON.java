package com.github.ilooner.polygoncli.client.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StockTradeJSON {
    private long t;
    private long y;
    private long q;
    private String i;
    private int x;
    private long s;
    private int[] c = new int[0];
    private double p;
    private int z;
}
