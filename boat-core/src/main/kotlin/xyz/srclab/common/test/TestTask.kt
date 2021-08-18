package xyz.srclab.common.test

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME

/**
 * Test task.
 */
interface TestTask {

    @get:JvmName("name")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        get() = this.javaClass.name

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
        fun newTask(times: Long, task: Runnable): TestTask {
            return newTask(times) { task.run() }
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

        @JvmStatic
        @JvmOverloads
        fun newTask(name: String? = null, times: Long = 1, task: Runnable): TestTask {
            return newTask(name, times) { task.run() }
        }
    }
}