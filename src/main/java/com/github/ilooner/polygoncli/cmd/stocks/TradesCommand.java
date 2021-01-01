package com.github.ilooner.polygoncli.cmd.stocks;

import com.beust.jcommander.Parameter;
import com.github.ilooner.polygoncli.client.PolygonClient;
import com.github.ilooner.polygoncli.client.model.Trade;
import com.github.ilooner.polygoncli.cmd.SourceCommand;
import com.github.ilooner.polygoncli.cmd.utils.LocalDateConverter;
import com.github.ilooner.polygoncli.output.Outputter;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import java.time.LocalDate;

public class TradesCommand extends StocksCommand implements SourceCommand {
    public static final String NAME = "trades";

    @Parameter(names = { "-t", "--ticker" },
            required = true,
            description = "The ticker symbol we want trades for.")
    private String ticker;

    @Parameter(names = "-st", description = "The start time.", converter = LocalDateConverter.class)
    private LocalDate startTime;

    @Parameter(names = "-et", description = "The end time.", converter = LocalDateConverter.class)
    private LocalDate endTime;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Schema getSchema() {
        return Trade.getClassSchema();
    }

    @Override
    public void run(PolygonClient client, Outputter outputter) {

    }
}
