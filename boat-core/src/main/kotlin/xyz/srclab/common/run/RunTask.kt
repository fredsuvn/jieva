package xyz.srclab.common.run

/**
 * Task to run by [Runner].
 *
 * [run] method is main body of the task, it will be called in specified run context of [Runner] or [Scheduler].
 * [initialize] method is used to do prepared initialized works in current run context.
 */
interface RunTask<V> {

    /**
     * Prepared works before running, will be called in current run context
     */
    fun initialize()

    /**
     * Run the task, will be called in specified run context.
     */
    fun run(): V
}