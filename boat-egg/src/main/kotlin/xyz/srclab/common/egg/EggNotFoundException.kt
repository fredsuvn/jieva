package xyz.srclab.common.egg

/**
 * Egg not found exception.
 */
class EggNotFoundException(
    egg: String, cause: Throwable? = null
) : RuntimeException(
    "Egg *$egg* not found. You chose the wrong egg, hero.",
    cause
)