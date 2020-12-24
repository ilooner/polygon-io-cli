package com.github.ilooner.polygoncli.cmd.output;

import com.github.ilooner.polygoncli.cmd.Command;
import com.github.ilooner.polygoncli.output.Outputter;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public interface OutputCommand extends Command {
    List<OutputCommand> COMMANDS = Collections.unmodifiableList(
            Lists.newArrayList(new OutputCSVCommand()));

    Outputter getOutputter();
}
