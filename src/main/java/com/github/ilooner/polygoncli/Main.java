package com.github.ilooner.polygoncli;

import com.beust.jcommander.JCommander;
import com.github.ilooner.polygoncli.cmd.SourceCommand;
import com.github.ilooner.polygoncli.cmd.output.OutputCommand;
import com.github.ilooner.polygoncli.cmd.stocks.StocksCommand;

public class Main {
    public static void main(String[] args) {
        createCommander();
    }

    public static JCommander createCommander() {
        final JCommander root = new JCommander();

        for (SourceCommand sourceCommand: StocksCommand.SOURCE_COMMANDS) {
            final JCommander sourceCommander = addSourceCommand(root, sourceCommand);

            for (OutputCommand outputCommand: OutputCommand.COMMANDS) {
                sourceCommander.addCommand(outputCommand.getName(), outputCommand);
            }
        }

        return root;
    }

    public static JCommander addSourceCommand(final JCommander root,
                                              final SourceCommand sourceCommand) {
        JCommander current = root;

        for (int i = 0; i < (sourceCommand.getPath().size() - 1); i++) {
            final String pathComponent = sourceCommand.getPath().get(i);
            JCommander next = current.getCommands().get(pathComponent);

            if (next == null) {
                current.addCommand(pathComponent, new Object());
                next = current.getCommands().get(pathComponent);
            }

            current = next;
        }

        current.addCommand(sourceCommand.getName(), sourceCommand);
        return current.getCommands().get(sourceCommand.getName());
    }
}
