package xyz.srclab.common.run

import java.util.concurrent.Executors

/**
 * A type of [Runner] with [Executors.newCachedThreadPool].
 */
object AsyncRunner : Runner by Runner.newExecutorServiceRunner(Executors.newCachedThreadPool())