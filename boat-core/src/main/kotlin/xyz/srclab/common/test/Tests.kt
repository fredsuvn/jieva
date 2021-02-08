@file:JvmName("Tests")
@file:JvmMultifileClass

package xyz.srclab.common.test

import xyz.srclab.common.collect.asToList
import xyz.srclab.common.run.AsyncRunner
import java.util.concurrent.Executor

fun testTasks(vararg tasks: TestTask) {
    testTasks(tasks.toList())
}

fun testTasks(tasks: Iterable<TestTask>) {
    testTasks0(tasks = tasks)
}

fun testTasks(tasks: Iterable<TestTask>, listener: TestListener) {
    testTasks0(tasks = tasks, listener = listener)
}

fun testTasksParallel(vararg tasks: TestTask) {
    testTasksParallel(tasks.toList())
}

fun testTasksParallel(tasks: Iterable<TestTask>) {
    testTasks0(executor = AsyncRunner, tasks = tasks)
}

fun testTasksParallel(tasks: Iterable<TestTask>, listener: TestListener) {
    testTasks0(AsyncRunner, tasks, listener)
}

fun testTasks(executor: Executor, vararg tasks: TestTask) {
    testTasks(executor, tasks.toList())
}

fun testTasks(executor: Executor, tasks: Iterable<TestTask>) {
    testTasks0(executor = executor, tasks = tasks)
}

fun testTasks(executor: Executor, tasks: Iterable<TestTask>, listener: TestListener) {
    testTasks0(executor, tasks, listener)
}

private fun testTasks0(
    executor: Executor = Executor { command -> command.run() },
    tasks: Iterable<TestTask>,
    listener: TestListener = TestListener.DEFAULT,
) {
    Tester.newTester(executor).start(tasks, listener)
}

fun equalsIgnoreOrder(a: Iterable<*>, b: Iterable<*>): Boolean {
    val ca = a.asToList()
    val cb = b.asToList()
    return ca.containsAll(cb) && cb.containsAll(ca)
}