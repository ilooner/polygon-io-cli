package com.github.ilooner.polygoncli.client.model.json;

import com.github.ilooner.polygoncli.client.model.Trade;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StockTradeJSON implements JSONData<Trade> {
    private String I = "";
    private long t;
    private long y;
    private long q;
    private String i;
    private int x;
    private long s;
    private List<Integer> c = new ArrayList<>();
    private double p;
    private int z;
    private int e;
    private int r;

    @Override
    public long getSeqNo() {
        return q;
    }

    @Override
    public long getTimestampMillis() {
        // Convert to millis
        return t / 1_000_000L;
    }

    @Override
    public Trade convert() {
        final var trade = new Trade();

        trade.setOriginalTradeID(I);
        trade.setExchangeID(x);
        trade.setPrice(p);
        trade.setTradeID(i);
        trade.setCorrectionIndicator(e);
        trade.setReportingFacilityID(r);
        trade.setTimestampNano(t);
        trade.setQuoteTimestampNano(y);
        trade.setSequenceNo(q);
        trade.setSize(s);
        trade.setTape(z);

        return trade;
    }
}
