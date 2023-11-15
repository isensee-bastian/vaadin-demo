package com.github.isenseebastian.vaadindemo.session;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

// TimeoutScheduler encapsulates a simple background thread that executes the registered callbacks every second.
// For now, it uses only a single thread. This can be configured to a bigger thread pool at a later point when needed.
// When used from views, callbacks MUST ensure that they are non-blocking and any UI access goes through the
// UI thread (i.e. utilize ui.access methods).
@Service
public class TimeoutScheduler {

    private final ScheduledExecutorService executor;

    public TimeoutScheduler() {
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    @PreDestroy
    public void cleanup() {
        executor.shutdown();
    }

    public ScheduledFuture<?> scheduleCallbackEachSecond(Runnable callback) {
        return executor.scheduleAtFixedRate(callback, 1L, 1L, TimeUnit.SECONDS);
    }
}
