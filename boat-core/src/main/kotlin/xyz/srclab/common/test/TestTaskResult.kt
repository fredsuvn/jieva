package xyz.srclab.common.test

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import java.time.Duration

interface TestTaskResult {

    @get:JvmName("cost")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val cost: Duration
}