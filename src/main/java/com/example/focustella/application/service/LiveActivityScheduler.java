package com.example.focustella.application.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LiveActivityScheduler {

    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public LiveActivityScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void schedule(String focusSessionId, Instant when, Runnable task) {
        cancel(focusSessionId);

        ScheduledFuture<?> future = taskScheduler.schedule(task, when);
        if (future != null) {
            scheduledTasks.put(focusSessionId, future);
            log.info("Scheduled live activity update: focusSessionId={}, at={}", focusSessionId, when);
        }
    }

    public void cancel(String focusSessionId) {
        ScheduledFuture<?> future = scheduledTasks.remove(focusSessionId);
        if (future != null) {
            future.cancel(false);
            log.info("Cancelled live activity schedule: focusSessionId={}", focusSessionId);
        }
    }
}
