package com.github.ilooner.polygoncli.output;

import java.util.List;

public interface Outputter<T> {
    void output(List<T> record) throws Exception;
    void finish() throws Exception;
}
