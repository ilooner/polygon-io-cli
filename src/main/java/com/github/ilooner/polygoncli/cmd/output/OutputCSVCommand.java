package com.github.ilooner.polygoncli.cmd.output;

import com.github.ilooner.polygoncli.output.Outputter;

public class OutputCSVCommand implements OutputCommand {
    public static final String NAME = "csv";

    @Override
    public Outputter getOutputter() {
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
