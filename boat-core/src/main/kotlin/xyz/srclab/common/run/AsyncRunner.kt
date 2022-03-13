package xyz.srclab.common.run

import xyz.srclab.common.run.Runner.Companion.toRunner
import java.util.concurrent.Executors

/**
 * A type of [Runner] with [Executors.newCachedThreadPool].
 */
object AsyncRunner : Runner by Executors.newCachedThreadPool().toRunner()