package com.github.ilooner.polygoncli.cmd;

import java.util.List;

public interface PathCommand extends Command {
    List<String> getPath();
}
