package com.github.ilooner.polygoncli.cmd;

import com.github.ilooner.polygoncli.output.Outputter;

public interface SourceCommand extends PathCommand {
    void run(Outputter outputter);
}
