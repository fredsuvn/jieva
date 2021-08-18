package xyz.srclab.common.test

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import java.time.Duration

interface TestResult {

    @get:JvmName("awaitCost")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val awaitCost: Duration

    @get:JvmName("totalCost")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val totalCost: Duration

    @get:JvmName("averageCost")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val averageCost: Duration
}