package com.github.ilooner.polygoncli.cmd;

import com.github.ilooner.polygoncli.out.Outputter;

import java.util.List;

public interface SourceCommand extends Command {
    List<String> getPath();
    void run(Outputter outputter);
}
