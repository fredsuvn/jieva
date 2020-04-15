package xyz.srclab.annotations.concurrent;

public enum ThreadSafeWhen {

    /**
     * Means thread-safe.
     */
    TRUE,

    /**
     * Means non-thread-safe or not.
     */
    FALSE,

    /**
     * Means whether thread-safe is depend on its dependents such as field, parameter, etc.
     */
    DEPEND_ON,
    ;
}
