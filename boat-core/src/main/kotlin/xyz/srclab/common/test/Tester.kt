package xyz.srclab.common.test

import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor

/**
 * To start [TestTask].
 */
interface Tester {

    fun start(tasks: Iterable<TestTask>, listener: TestListener)

    companion object {

        @JvmStatic
        fun newTester(executor: Executor = Executor { command -> command.run() }): Tester {
            return TesterImpl(executor)
        }

        private class TesterImpl(private val executor: Executor) : Tester {

            override fun start(tasks: Iterable<TestTask>, listener: TestListener) {
                val tests = tasks.toList()
                val counter = CountDownLatch(tests.size)

                listener.beforeRunAll(tests)

                val costs = mutableListOf<Duration>()

                val awaitStartTime = LocalDateTime.now()
                for (testTask in tests) {
                    executor.execute {
                        listener.beforeRunEach(testTask)
                        val startTime = LocalDateTime.now()
                        testTask.run()
                        val endTime = LocalDateTime.now()
                        val cost = Duration.between(startTime, endTime)
                        listener.afterRunEach(testTask, object : TestTaskResult {
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
                val averageCost = totalCost.dividedBy(tests.size.toLong())
                listener.afterRunAll(tests, object : TestResult {
                    override val awaitCost: Duration = awaitCost
                    override val totalCost: Duration = totalCost
                    override val averageCost: Duration = averageCost
                })
            }
        }
    }
}