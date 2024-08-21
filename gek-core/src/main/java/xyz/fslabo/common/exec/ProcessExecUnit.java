package xyz.fslabo.common.exec;

import xyz.fslabo.annotations.Nullable;

import java.util.function.Supplier;

/**
 * {@link Process}-based implementation of {@link ExecUnit}.
 *
 * @author fredsuvn
 */
public class ProcessExecUnit implements ExecUnit {

    private final Supplier<Process> supplier;
    private volatile @Nullable Process process;

    ProcessExecUnit(Supplier<Process> supplier) {
        this.supplier = supplier;
    }

    @Override
    public synchronized void start() {
        process = supplier.get();
    }

    @Override
    public synchronized ExecState getState() {
        if (process == null) {
            return ExecState.NEW;
        }
        if (process.isAlive()) {
            return ExecState.RUNNING;
        }
        return ExecState.TERMINATED;
    }

    @Override
    public synchronized boolean isStarted() {
        return process != null;
    }

    @Override
    public synchronized boolean isAlive() {
        return process != null && process.isAlive();
    }

    @Override
    public synchronized boolean isTerminated() {
        return process != null && !process.isAlive();
    }

    /**
     * Returns underlying process object, may be {@code null} if the process is not ready.
     *
     * @return underlying process object, may be {@code null} if the process is not ready
     */
    @Nullable
    public Process getProcess() {
        return process;
    }
}
