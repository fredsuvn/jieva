package test.xyz.srclab.test.tester

import xyz.srclab.test.tester.TestListener
import xyz.srclab.test.tester.TestTask
import xyz.srclab.test.tester.testTasks
import java.util.*
import java.util.concurrent.Executors
import kotlin.test.Test

object TesterTestKt {

    @Test
    fun testTester() {
        testTasks(
            Executors.newCachedThreadPool(),
            TestListener.DEFAULT,
            TestTaskImpl("task1"),
            TestTaskImpl("task2"),
            TestTaskImpl("task3"),
            TestTaskImpl("task4"),
            TestTaskImpl("task5")
        )
    }

    private class TestTaskImpl(override val name: String) : TestTask {

        override fun run() {
            try {
                Thread.sleep(Random().nextInt(5) * 1000.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}

