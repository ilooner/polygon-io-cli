package com.github.ilooner.polygoncli.output;

import org.apache.avro.generic.GenericRecord;

import java.io.IOException;

public class ConsoleOutputter implements Outputter<GenericRecord> {
    @Override
    public void output(GenericRecord record) {

    }

    @Override
    public void finish() throws IOException, InterruptedException {

    }
}
