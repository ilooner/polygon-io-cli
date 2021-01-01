package com.github.ilooner.polygoncli.cmd.utils;

import com.beust.jcommander.IStringConverter;
import org.joda.time.LocalDate;

import static com.github.ilooner.polygoncli.client.PolygonClient.DATE_TIME_FORMATTER;

public class LocalDateConverter implements IStringConverter<LocalDate> {
    @Override
    public LocalDate convert(String value) {
        return DATE_TIME_FORMATTER.parseLocalDate(value);
    }
}
