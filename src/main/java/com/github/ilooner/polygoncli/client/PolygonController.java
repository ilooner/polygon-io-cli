package com.github.ilooner.polygoncli.client;

import com.github.ilooner.polygoncli.config.PolygonConfig;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

@RequiredArgsConstructor
public class PolygonController {
    public static final String POLYGON_ENDPOINT = "https://api.polygon.io";

    private final PolygonConfig config;

    public PolygonAPI build() {
        final var httpClient = new OkHttpClient.Builder()
                .addInterceptor(new APIKeyInterceptor(config))
                .build();

        return new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(POLYGON_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build()
                .create(PolygonAPI.class);
    }

    @RequiredArgsConstructor
    public static class APIKeyInterceptor implements Interceptor {
        private final PolygonConfig config;

        @Override
        public Response intercept(Chain chain) throws IOException {
            final var original = chain.request();
            final var url = original.url()
                    .newBuilder()
                    .addQueryParameter("apiKey", config.getApiKey())
                    .build();
            final var request = original.newBuilder()
                    .url(url)
                    .build();
            return chain.proceed(request);
        }
    }
}
