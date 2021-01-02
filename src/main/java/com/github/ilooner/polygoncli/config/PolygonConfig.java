package com.github.ilooner.polygoncli.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolygonConfig {
    private String apiKey;
    private boolean limited;

    public PolygonConfig() {
    }
}
