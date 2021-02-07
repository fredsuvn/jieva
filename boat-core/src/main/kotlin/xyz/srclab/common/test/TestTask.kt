package xyz.srclab.common.test

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.toTimestamp
import java.time.Duration
import java.time.ZonedDateTime

/**
 * Test task.
 */
interface TestTask {

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val name: String
        @JvmName("name") get() = this.javaClass.name

    fun run()

    companion object {

        @JvmStatic
        fun newTask(times: Long, task: () -> Unit): TestTask {
            return object : TestTask {
                override fun run() {
                    for (i in 1..times) {
                        task()
                    }
                }
            }
        }

        @JvmStatic
        @JvmOverloads
        fun newTask(name: String? = null, times: Long = 1, task: () -> Unit): TestTask {
            return object : TestTask {
                override val name: String = name ?: this.javaClass.name
                override fun run() {
                    for (i in 1..times) {
                        task()
                    }
                }
            }
        }
    }
}

interface TestListener {

    fun beforeRunAll(testTasks: List<TestTask>)

    fun beforeRunEach(testTask: TestTask)

    fun afterRunEach(testTask: TestTask, testTaskResult: TestTaskResult)

    fun afterRunAll(testTasks: List<TestTask>, testResult: TestResult)

    companion object {

        @JvmField
        val EMPTY: TestListener = object : TestListener {

            override fun beforeRunAll(testTasks: List<TestTask>) {}

            override fun beforeRunEach(testTask: TestTask) {}

            override fun afterRunEach(testTask: TestTask, testTaskResult: TestTaskResult) {}

            override fun afterRunAll(testTasks: List<TestTask>, testResult: TestResult) {}
        }

        @JvmField
        val DEFAULT: TestListener = withTestLogger(TestLogger.DEFAULT)

        @JvmStatic
        fun withTestLogger(testLogger: TestLogger): TestListener {
            return object : TestListener {

                override fun beforeRunAll(testTasks: List<TestTask>) {
                    testLogger.log("At ${ZonedDateTime.now().toTimestamp()} prepare to run all tasks...")
                }

                override fun beforeRunEach(testTask: TestTask) {
                    testLogger.log("Run task ${testTask.name}...")
                }

                override fun afterRunEach(testTask: TestTask, testTaskResult: TestTaskResult) {
                    testLogger.log("Task ${testTask.name} was accomplished, cost: ${testTaskResult.cost}")
                }

                override fun afterRunAll(testTasks: List<TestTask>, testResult: TestResult) {
                    testLogger.log(
                        "All tasks were accomplished, " +
                                "await cost: ${testResult.awaitCost}, " +
                                "total cost: ${testResult.totalCost}, " +
                                "average cost: ${testResult.averageCost}"
                    )
                }
            }
        }
    }
}

interface TestTaskResult {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val cost: Duration
        @JvmName("cost") get
}

interface TestResult {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val awaitCost: Duration
        @JvmName("awaitCost") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val totalCost: Duration
        @JvmName("totalCost") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val averageCost: Duration
        @JvmName("averageCost") get
}