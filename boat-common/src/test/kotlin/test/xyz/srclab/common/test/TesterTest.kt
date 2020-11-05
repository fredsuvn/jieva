package test.xyz.srclab.common.test

import xyz.srclab.common.test.TestListener
import xyz.srclab.common.test.TestTask
import xyz.srclab.common.test.TestTask.Companion.newTask
import xyz.srclab.common.test.testTasks
import java.util.*
import java.util.concurrent.Executors
import kotlin.test.Test

object TesterTestKt {

    private val random: Random = Random()

    @Test
    fun testTester() {
        testTasks(
            Executors.newCachedThreadPool(),
            TestListener.DEFAULT,
            newTask("task1"),
            newTask("task2"),
            newTask("task3"),
            newTask("task4"),
            newTask("task5")
        )
    }

    private fun newTask(name: String): TestTask {
        return newTask(name) {
            try {
                Thread.sleep(random.nextInt(5) * (random.nextInt(400) + 800).toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}