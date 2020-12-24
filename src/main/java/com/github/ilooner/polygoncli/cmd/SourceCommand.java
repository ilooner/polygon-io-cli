package com.github.ilooner.polygoncli.cmd;

import com.github.ilooner.polygoncli.output.Outputter;

import java.util.List;

public interface SourceCommand extends Command {
    List<String> getPath();
    void run(Outputter outputter);
}
