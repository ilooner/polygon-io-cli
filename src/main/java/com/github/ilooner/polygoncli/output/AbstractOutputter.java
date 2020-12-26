package com.github.ilooner.polygoncli.output;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractOutputter<T> implements Outputter<T> {
    private final Duration shutdownDuration;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Semaphore queueLimiter = new Semaphore(3);

    @Override
    public void finish() throws IOException, InterruptedException {
        executorService.shutdown();

        try {
            executorService.awaitTermination(shutdownDuration.toMillis(), TimeUnit.MILLISECONDS);
        } finally {
            onFinish();
        }
    }

    protected abstract void onFinish() throws IOException;
}
