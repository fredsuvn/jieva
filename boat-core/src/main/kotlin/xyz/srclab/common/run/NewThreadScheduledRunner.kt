package xyz.srclab.common.run

import java.time.Duration

private val newThreadScheduledRunner: ScheduledRunner by lazy {
    ScheduledThreadPoolRunner.Builder()
        .corePoolSize(0)
        .threadFactory { r -> Thread(r) }
        .keepAliveTime(Duration.ZERO)
        .build()
}

object NewThreadScheduledRunner : ScheduledRunner by newThreadScheduledRunner