package com.github.ilooner.polygoncli.client.model.json;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StockExchangeJSON {
    private int id;
    private String type;
    private String market;
    private String mic;
    private String name;
    private String tape;
}
