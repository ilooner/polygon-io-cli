package com.github.ilooner.polygoncli.cmd.stocks;

import com.github.ilooner.polygoncli.cmd.SourceCommand;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public abstract class StocksCommand implements SourceCommand {
    public static final String PATH = "stocks";
    public static final List<SourceCommand> SOURCE_COMMANDS = Collections.unmodifiableList(
            Lists.newArrayList(new TradesCommand(), new AggregatesCommand())
    );

    @Override
    public List<String> getPath() {
        return Collections.unmodifiableList(Lists.newArrayList(
                PATH,
                getName()));
    }
}
