package xyz.srclab.common.lang

/**
 * Represents next operation, usually used for the object which delegate performance to a group of handlers.
 *
 * For three values:
 *
 * * If one of handlers returns [CONTINUE], means that handler failed to convert and suggests continue to next handler;
 * * If returns [BREAK], means that handler failed to convert and suggests break handler chain;
 * * If returns [COMPLETE], means that handler success and complete conversation;
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

    /**
     * Represents current handler success and complete conversation.
     */
    COMPLETE,
}