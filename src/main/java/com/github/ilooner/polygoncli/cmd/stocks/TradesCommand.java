package com.github.ilooner.polygoncli.cmd.stocks;

import com.beust.jcommander.Parameter;
import com.github.ilooner.polygoncli.cmd.SourceCommand;
import com.github.ilooner.polygoncli.output.Outputter;

import java.util.Date;

public class TradesCommand extends StocksCommand implements SourceCommand {
    public static final String NAME = "trades";

    @Parameter(names = { "-t", "--ticker" },
            required = true,
            description = "The ticker symbol we want trades for.")
    private String ticker;

    @Parameter(names = "-st", description = "The start time.")
    private Date startTime;

    @Parameter(names = "-et", description = "The end time.")
    private Date endTime;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void run(Outputter outputter) {

    }
}
