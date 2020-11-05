package test.xyz.srclab.common.test

import xyz.srclab.common.test.TestListener
import xyz.srclab.common.test.TestTask
import xyz.srclab.common.test.testTasks
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
                Thread.sleep(random.nextInt(5) * (random.nextInt(400) + 800).toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        companion object {

            private val random = Random()
        }
    }
}