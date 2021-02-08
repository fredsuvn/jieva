package sample.xyz.srclab.common.run

import org.testng.annotations.Test
import xyz.srclab.common.base.Current.sleep
import xyz.srclab.common.base.IntRef.Companion.of
import xyz.srclab.common.run.Runner
import xyz.srclab.common.run.Running
import xyz.srclab.common.run.ScheduledRunner
import xyz.srclab.common.run.ScheduledRunning
import xyz.srclab.common.test.TestLogger
import java.time.Duration

class RunSampleKt {

    @Test
    fun testRunner() {
        val runner: Runner = Runner.SYNC_RUNNER
        val intRef = of(0)
        val running: Running<*> = runner.run<Any?> {
            intRef.set(666)
            null
        }
        running.get()
        //666
        logger.log("int: {}", intRef.get())
    }

    @Test
    fun testScheduledRunner() {
        val runner = ScheduledRunner.SINGLE_THREAD_RUNNER
        val intRef = of(0)
        val running: ScheduledRunning<*> = runner.scheduleWithFixedDelay<Any?>(Duration.ZERO, Duration.ofMillis(1000)) {
            intRef.set(intRef.get() + 100)
            null
        }
        sleep(2500)
        running.cancel(false)
        //300
        logger.log("int: {}", intRef.get())
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}