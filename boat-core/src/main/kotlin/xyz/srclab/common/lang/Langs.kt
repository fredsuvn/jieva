package xyz.srclab.common.lang

/**
 * Used for the object which delegate performance to a group of handlers.
 *
 * If one of handlers returns [CONTINUE], means that handler failed to convert and suggests continue to next handler;
 * if returns [BREAK], means that handler failed to convert and suggests break handler chain.
 */
enum class Next {

    /**
     * Represents current handler failed to convert and suggests continue to next handler.
     */
    CONTINUE,

    /**
     * Represents current handler failed to convert and suggests break handler chain.
     */
    BREAK,
}