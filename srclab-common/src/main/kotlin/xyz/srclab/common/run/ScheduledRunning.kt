package xyz.srclab.common.run

import java.util.concurrent.ScheduledFuture

interface ScheduledRunning<V> : Running<V>, ScheduledFuture<V>