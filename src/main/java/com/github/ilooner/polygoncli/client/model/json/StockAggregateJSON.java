package com.github.ilooner.polygoncli.client.model.json;

import com.github.ilooner.polygoncli.client.model.Aggregate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StockAggregateJSON implements JSONData<Aggregate> {
    private double o;
    private double h;
    private double l;
    private double c;
    private long v;
    private double vw;
    private long t;
    private long n;

    @Override
    public long getTimestampMillis() {
        return t;
    }

    public Aggregate convert() {
        final Aggregate aggregate = new Aggregate();

        aggregate.setOpenPrice(o);
        aggregate.setHighestPrice(h);
        aggregate.setLowestPrice(l);
        aggregate.setClosePrice(c);
        aggregate.setVolume(v);
        aggregate.setAverage(vw);
        aggregate.setTimestamp(t);
        aggregate.setNumberOfItems(n);

        return aggregate;
    }
}
