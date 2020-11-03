package xyz.srclab.test.perform;

/**
 * @author sunqian
 */
public class PerformInfo {

    private final String title;
    private final Runnable action;

    public PerformInfo(String title, Runnable action) {
        this.title = title;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public Runnable getAction() {
        return action;
    }
}
