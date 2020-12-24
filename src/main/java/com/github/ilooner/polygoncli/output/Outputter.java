package com.github.ilooner.polygoncli.output;

import java.io.IOException;

public interface Outputter {
    void finish() throws IOException, InterruptedException;
}
