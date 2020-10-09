package xyz.srclab.common.exception

open class RuntimeExceptionWrapper(val wrapped: Throwable) : RuntimeException(wrapped)