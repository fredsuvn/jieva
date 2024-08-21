package xyz.fslabo.common.exec;

/**
 * This interface represents an execution unit, may be a thread, a process, or other executable things.
 *
 * @author fredsuvn
 */
public interface ExecUnit {

    /**
     * Starts this execution.
     */
    void start();

    /**
     * Returns state of this execution unit.
     *
     * @return state of this execution unit
     */
    ExecState getState();

    /**
     * Returns whether this execution unit is started.
     *
     * @return whether this execution unit is started
     */
    boolean isStarted();

    /**
     * Returns whether this execution unit is alive. An execution unit is alive if it has been started and has not yet
     * died.
     *
     * @return whether this execution unit is alive
     */
    boolean isAlive();

    /**
     * Returns whether this execution unit is terminated.
     *
     * @return whether this execution unit is terminated
     */
    boolean isTerminated();
}
