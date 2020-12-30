
package com.github.ilooner.polygoncli.output;

import org.apache.avro.generic.GenericRecord;

import java.io.IOException;
import java.util.List;

public class ConsoleOutputter implements Outputter<GenericRecord> {
    @Override
    public void output(List<GenericRecord> record) throws IOException {

    }

    @Override
    public void finish() throws IOException, InterruptedException {

    }
}
