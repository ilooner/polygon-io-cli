package com.github.ilooner.polygoncli.output;

import org.apache.avro.generic.GenericRecord;

import java.io.IOException;
import java.time.Duration;

public class CSVOutputter extends AbstractOutputter<GenericRecord> {
    protected CSVOutputter(Duration shutdownDuration) {
        super(shutdownDuration);
    }

    @Override
    public void output(GenericRecord record) {

    }

    @Override
    public void finish() throws IOException {
    }

    @Override
    protected void onFinish() throws IOException {

    }
}
