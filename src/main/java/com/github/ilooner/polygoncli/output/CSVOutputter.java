package com.github.ilooner.polygoncli.output;

import com.google.common.base.Charsets;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CSVOutputter extends AbstractOutputter<GenericRecord> {
    private static final byte[] NEWLINE_BYTES = "\n".getBytes(Charsets.UTF_8);

    private final OutputStream outputStream;
    private final Schema schema;
    private final List<String> tmpList = new ArrayList<>();

    protected CSVOutputter(final Duration shutdownDuration,
                           final Schema schema,
                           final Path path,
                           final boolean header) throws IOException {
        super(shutdownDuration);

        this.outputStream = new BufferedOutputStream(Files.newOutputStream(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE));
        this.schema = schema;

        final var headers = schema.getFields()
                .stream()
                .map(field -> field.name())
                .collect(Collectors.toList());

        if (header) {
            writeRow(headers);
        }
    }

    @Override
    public void writeRecord(GenericRecord record) throws IOException {
        for (int i = 0; i < schema.getFields().size(); i++) {
            tmpList.add(record.get(i).toString());
        }

        writeRow(tmpList);
        tmpList.clear();
    }

    private void writeRow(List<String> row) throws IOException {
        final byte[] bytes = String.join(",", row).getBytes(Charsets.UTF_8);
        outputStream.write(bytes);
        outputStream.write(NEWLINE_BYTES);
    }

    @Override
    protected void onFinish() throws IOException {
        outputStream.flush();
        outputStream.close();
    }

    public static class Builder {
        private boolean header;

        public Builder setHeader(final boolean header) {
            this.header = header;
            return this;
        }

        public CSVOutputter build(final Schema schema,
                                  final Path path) throws IOException {
            return new CSVOutputter(Duration.ofSeconds(30L), schema, path, header);
        }
    }
}
