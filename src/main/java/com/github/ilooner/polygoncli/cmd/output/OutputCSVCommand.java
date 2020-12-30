package com.github.ilooner.polygoncli.cmd.output;

import com.beust.jcommander.Parameter;
import com.github.ilooner.polygoncli.output.CSVOutputter;
import com.github.ilooner.polygoncli.output.Outputter;
import org.apache.avro.Schema;

import java.io.IOException;
import java.nio.file.Path;

public class OutputCSVCommand implements OutputCommand {
    public static final String NAME = "csv";

    @Parameter(names = { "-h", "--header" }, description = "Flag to include header.")
    private boolean header;

    @Parameter(names = {"-o", "--output"}, required = true, description = "Output file.")
    private Path path;

    @Override
    public Outputter getOutputter(Schema schema) throws IOException {
        return new CSVOutputter.Builder().setHeader(header).build(schema, path);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
