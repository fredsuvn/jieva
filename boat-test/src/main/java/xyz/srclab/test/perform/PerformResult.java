package xyz.srclab.test.perform;

import java.time.Duration;

/**
 * @author sunqian
 */
public class PerformResult {

    private final String title;
    private final Duration costTime;

    public PerformResult(String title, Duration costTime) {
        this.title = title;
        this.costTime = costTime;
    }

    public String getTitle() {
        return title;
    }

    public Duration getCostTime() {
        return costTime;
    }
}
