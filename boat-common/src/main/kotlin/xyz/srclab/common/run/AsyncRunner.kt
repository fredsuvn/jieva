package xyz.srclab.common.run

import java.util.concurrent.Executors

private val asyncRunner = Runner.newExecutorServiceRunner(Executors.newCachedThreadPool())

object AsyncRunner : Runner by asyncRunner