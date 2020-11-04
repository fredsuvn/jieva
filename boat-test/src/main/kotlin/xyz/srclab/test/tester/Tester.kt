@file:JvmName("Tester")

package xyz.srclab.test.tester

import xyz.srclab.kotlin.compile.COMPILE_INAPPLICABLE_JVM_NAME
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor

fun testTasks(testTasks: Iterable<TestTask>) {
    testTasks0(testTasks)
}

fun testTasks(testTasks: Iterable<TestTask>, testListener: TestListener) {
    testTasks0(testTasks, testListener)
}

fun testTasks(testTasks: Iterable<TestTask>, executor: Executor) {
    testTasks0(testTasks, executor = executor)
}

fun testTasks(testTasks: Iterable<TestTask>, testListener: TestListener, executor: Executor) {
    testTasks0(testTasks, testListener, executor)
}

private fun testTasks0(
    testTasks: Iterable<TestTask>,
    testListener: TestListener = TestListener.EMPTY,
    executor: Executor = Executor { command -> command.run() },
) {
    val tasks = testTasks.toList()
    val counter = CountDownLatch(tasks.size)
    testListener.beforeRun(tasks)
    val costs = mutableListOf<Duration>()
    for (testTask in testTasks) {
        executor.execute {
            testListener.beforeEachRun(testTask)
            val startTime = LocalDateTime.now()
            testTask.run()
            val endTime = LocalDateTime.now()
            val cost = Duration.between(startTime, endTime)
            testListener.afterEachRun(testTask, object : TestTaskExpense {
                override val cost: Duration = cost
            })
            synchronized(counter) {
                costs.add(cost)
                counter.countDown()
            }
        }
    }
    counter.await()

    val totalCost = costs.reduce { d1, d2 -> d1.plus(d2) }
    val averageCost = totalCost.dividedBy(tasks.size.toLong())
    testListener.afterRun(tasks, object : TestTasksExpense {
        override val cost: Duration = totalCost
        override val average: Duration = averageCost
    })
}

interface TestTask {

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    fun run()
}

interface TestListener {

    fun beforeRun(testTasks: List<TestTask>)

    fun beforeEachRun(testTask: TestTask)

    fun afterEachRun(testTask: TestTask, testExpense: TestTaskExpense)

    fun afterRun(testTasks: List<TestTask>, testExpense: TestTasksExpense)

    companion object {

        @JvmField
        val EMPTY: TestListener = object : TestListener {

            override fun beforeRun(testTasks: List<TestTask>) {}

            override fun beforeEachRun(testTask: TestTask) {}

            override fun afterEachRun(testTask: TestTask, testExpense: TestTaskExpense) {}

            override fun afterRun(testTasks: List<TestTask>, testExpenses: TestTasksExpense) {}
        }
    }
}

interface TestTaskExpense {

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val cost: Duration
        @JvmName("cost") get
}

interface TestTasksExpense {

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val cost: Duration
        @JvmName("cost") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val average: Duration
        @JvmName("average") get
}