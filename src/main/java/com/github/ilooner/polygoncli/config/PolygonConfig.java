package com.github.ilooner.polygoncli.config;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class PolygonConfig {
    private String apiKey;

    public PolygonConfig() {
    }
}
