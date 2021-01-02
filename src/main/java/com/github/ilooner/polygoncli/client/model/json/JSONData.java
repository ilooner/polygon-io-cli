package com.github.ilooner.polygoncli.client.model.json;

import org.apache.avro.specific.SpecificRecord;

public interface JSONData<T extends SpecificRecord> {
    long getTimestampMillis();

    T convert();
}
