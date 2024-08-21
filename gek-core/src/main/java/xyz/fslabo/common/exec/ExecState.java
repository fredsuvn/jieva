package xyz.fslabo.common.exec;

/**
 * State of {@link ExecUnit}.
 *
 * @author sunq62
 */
public enum ExecState {

    /**
     * State represents the {@link ExecUnit} is creating and preparing to start.
     */
    NEW,

    /**
     * State represents the {@link ExecUnit} is ready but has not yet started.
     */
    READY,

    /**
     * State represents the {@link ExecUnit} is running.
     */
    RUNNING,

    /**
     * State represents the {@link ExecUnit} is blocked or suspended.
     */
    WAITING,

    /**
     * State represents the {@link ExecUnit} is terminated. The {@link ExecUnit} has completed execution.
     */
    TERMINATED,

    /**
     * Unexpected state.
     */
    UNEXPECTED,
}
