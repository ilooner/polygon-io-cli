package com.github.ilooner.polygoncli.output;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractOutputter<T> implements Outputter<T> {
    private final Duration shutdownDuration;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Semaphore queueLimiter = new Semaphore(3);
    private final List<Future<Void>> futureList = new LinkedList<>();

    @Override
    public void finish() throws IOException, InterruptedException {
        executorService.shutdown();

        try {
            executorService.awaitTermination(shutdownDuration.toMillis(), TimeUnit.MILLISECONDS);
        } finally {
            onFinish();
        }
    }

    @Override
    public void output(final List<T> records) throws Exception {
        final var it = futureList.listIterator();

        while (it.hasNext()) {
            final var future = it.next();

            if (!future.isDone()) {
                continue;
            }

            try {
                future.get();
            } catch (Exception e) {
                throw e;
            }

            it.remove();
        }

        executorService.submit(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                for (T record: records) {
                    writeRecord(record);
                }
            }
        });
    }

    protected abstract void writeRecord(T record) throws IOException;

    protected abstract void onFinish() throws IOException;
}
