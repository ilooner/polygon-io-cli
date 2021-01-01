package com.github.ilooner.polygoncli.cmd;

import com.github.ilooner.polygoncli.client.PolygonClient;
import com.github.ilooner.polygoncli.output.Outputter;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

public interface SourceCommand extends PathCommand {
    void run(PolygonClient client, Outputter<GenericRecord> outputter) throws Exception;
    Schema getSchema();
}
