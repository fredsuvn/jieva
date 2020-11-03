package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.SynchronousQueue

private val asyncRunner = ThreadPoolRunner.Builder()
    .corePoolSize(0)
    .maximumPoolSize(Int.MAX_VALUE)
    .keepAliveTime(Duration.ZERO)
    .workQueue(SynchronousQueue())
    .threadFactory { r -> Thread(r) }
    .build()

object AsyncRunner : Runner by asyncRunner