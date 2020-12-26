package com.github.ilooner.polygoncli.output;

import java.io.IOException;

public interface Outputter<T> {
    void output(T record);
    void finish() throws IOException, InterruptedException;
}
