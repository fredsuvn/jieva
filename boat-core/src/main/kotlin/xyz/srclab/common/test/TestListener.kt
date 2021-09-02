package xyz.srclab.common.test

import xyz.srclab.common.base.toTimestamp
import java.time.ZonedDateTime

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