package xyz.srclab.common.run

/**
 * Task to run by [Runner].
 *
 * [run] method is run body of the task, it will be run by specified [Runner].
 * [prepare] method is used to do prepared works (in current thread) before posted to [Runner].
 */
interface RunTask<V> {

    /**
     * Prepared works before posted to [Runner], will be called in current thread.
     */
    fun prepare()

    /**
     * Run the task, used by [Runner].
     */
    fun run(): V
}