package com.github.ilooner.polygoncli.cmd.stocks;

import com.github.ilooner.polygoncli.client.PolygonClient;
import com.github.ilooner.polygoncli.output.Outputter;
import org.apache.avro.Schema;

public class ExchangesCommand extends StocksCommand {
    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void run(PolygonClient client, Outputter outputter) {

    }
}
