package com.github.ilooner.polygoncli.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MemoryOutputter<T> implements Outputter<T> {
    private List<T> outputList = new ArrayList<>();

    @Override
    public void output(List<T> record) throws Exception {
        outputList.addAll(record);
    }

    @Override
    public void finish() throws IOException, InterruptedException {

    }

    public List<T> getOutputList() {
        return outputList;
    }

    public void clear() {
        outputList.clear();
    }
}
