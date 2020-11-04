@file:JvmName("Tester")

package xyz.srclab.test.tester

import xyz.srclab.jvm.compile.INAPPLICABLE_JVM_NAME
import java.io.PrintStream
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor

fun testTasks(vararg testTasks: TestTask) {
    testTasks(testTasks.toList())
}

fun testTasks(testTasks: Iterable<TestTask>) {
    testTasks0(testTasks = testTasks)
}

fun testTasks(testListener: TestListener, vararg testTasks: TestTask) {
    testTasks(testListener, testTasks.toList())
}

fun testTasks(testListener: TestListener, testTasks: Iterable<TestTask>) {
    testTasks0(testListener = testListener, testTasks = testTasks)
}

fun testTasks(executor: Executor, vararg testTasks: TestTask) {
    testTasks(executor, testTasks.toList())
}

fun testTasks(executor: Executor, testTasks: Iterable<TestTask>) {
    testTasks0(executor = executor, testTasks = testTasks)
}

fun testTasks(executor: Executor, testListener: TestListener, vararg testTasks: TestTask) {
    testTasks(executor, testListener, testTasks.toList())
}

fun testTasks(executor: Executor, testListener: TestListener, testTasks: Iterable<TestTask>) {
    testTasks0(executor, testListener, testTasks)
}

private fun testTasks0(
    executor: Executor = Executor { command -> command.run() },
    testListener: TestListener = TestListener.EMPTY,
    testTasks: Iterable<TestTask>,
) {
    val tasks = testTasks.toList()
    val counter = CountDownLatch(tasks.size)

    testListener.beforeRun(tasks)

    val costs = mutableListOf<Duration>()

    val awaitStartTime = LocalDateTime.now()
    for (testTask in testTasks) {
        executor.execute {
            testListener.beforeEachRun(testTask)
            val startTime = LocalDateTime.now()
            testTask.run()
            val endTime = LocalDateTime.now()
            val cost = Duration.between(startTime, endTime)
            testListener.afterEachRun(testTask, object : TestTaskResult {
                override val cost: Duration = cost
            })
            synchronized(counter) {
                costs.add(cost)
                counter.countDown()
            }
        }
    }
    counter.await()

    val awaitCost = Duration.between(awaitStartTime, LocalDateTime.now())
    val totalCost = costs.reduce { d1, d2 -> d1.plus(d2) }
    val averageCost = totalCost.dividedBy(tasks.size.toLong())
    testListener.afterRun(tasks, object : TestResult {
        override val awaitCost: Duration = awaitCost
        override val totalCost: Duration = totalCost
        override val averageCost: Duration = averageCost
    })
}

interface TestTask {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    fun run()
}

interface TestListener {

    fun beforeRun(testTasks: List<TestTask>)

    fun beforeEachRun(testTask: TestTask)

    fun afterEachRun(testTask: TestTask, testTaskResult: TestTaskResult)

    fun afterRun(testTasks: List<TestTask>, testResult: TestResult)

    companion object {

        @JvmField
        val EMPTY: TestListener = object : TestListener {

            override fun beforeRun(testTasks: List<TestTask>) {}

            override fun beforeEachRun(testTask: TestTask) {}

            override fun afterEachRun(testTask: TestTask, testTaskResult: TestTaskResult) {}

            override fun afterRun(testTasks: List<TestTask>, testResult: TestResult) {}
        }

        @JvmField
        val DEFAULT: TestListener = withPrintStream(System.out)

        @JvmStatic
        fun withPrintStream(printStream: PrintStream): TestListener {
            return object : TestListener {

                override fun beforeRun(testTasks: List<TestTask>) {}

                override fun beforeEachRun(testTask: TestTask) {
                    printStream.println("Run task ${testTask.name}...")
                }

                override fun afterEachRun(testTask: TestTask, testTaskResult: TestTaskResult) {
                    printStream.println("Task ${testTask.name} running complete, cost: ${testTaskResult.cost}")
                }

                override fun afterRun(testTasks: List<TestTask>, testResult: TestResult) {
                    printStream.println(
                        "All tasks running complete, " +
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