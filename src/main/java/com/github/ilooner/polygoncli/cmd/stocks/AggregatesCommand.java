package com.github.ilooner.polygoncli.cmd.stocks;

import com.beust.jcommander.Parameter;
import com.github.ilooner.polygoncli.client.PolygonClient;
import com.github.ilooner.polygoncli.client.model.Aggregate;
import com.github.ilooner.polygoncli.cmd.SourceCommand;
import com.github.ilooner.polygoncli.cmd.utils.LocalDateConverter;
import com.github.ilooner.polygoncli.output.Outputter;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.joda.time.LocalDate;

public class AggregatesCommand extends StocksCommand implements SourceCommand {
    public static final String NAME = "aggregates";

    @Parameter(names = { "-t", "--ticker" },
            required = true,
            description = "The ticker symbol we want trades for.")
    private String ticker;

    @Parameter(names = "-s", description = "The start date.", converter = LocalDateConverter.class)
    private LocalDate startDate;

    @Parameter(names = "-e", description = "The end date.", converter = LocalDateConverter.class)
    private LocalDate endDate;

    @Override
    public Schema getSchema() {
        return Aggregate.getClassSchema();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void run(PolygonClient client, Outputter<GenericRecord> outputter) throws Exception {
        client.outputStockAggregates(ticker, startDate, endDate, (Outputter) outputter);
    }
}
