package xyz.fslabo.common.exec;

/**
 * {@link Thread}-based implementation of {@link ExecUnit}.
 *
 * @author fredsuvn
 */
public class ThreadExecUnit implements ExecUnit {

    private final Thread thread;
    private volatile boolean isStarted = false;

    ThreadExecUnit(Thread thread) {
        this.thread = thread;
    }

    @Override
    public synchronized void start() {
        thread.start();
        isStarted = true;
    }

    @Override
    public synchronized ExecState getState() {
        if (!isStarted) {
            return ExecState.NEW;
        }
        Thread.State state = thread.getState();
        switch (state) {
            case NEW:
                return ExecState.READY;
            case RUNNABLE:
                return ExecState.RUNNING;
            case BLOCKED:
            case WAITING:
            case TIMED_WAITING:
                return ExecState.WAITING;
            case TERMINATED:
                return ExecState.TERMINATED;
        }
        return ExecState.UNEXPECTED;
    }

    @Override
    public synchronized boolean isStarted() {
        return isStarted;
    }

    @Override
    public synchronized boolean isAlive() {
        return thread.isAlive();
    }

    @Override
    public synchronized boolean isTerminated() {
        return isStarted && !thread.isAlive();
    }

    /**
     * Returns underlying thread object.
     *
     * @return underlying thread object
     */
    public Thread getThread() {
        return thread;
    }
}
