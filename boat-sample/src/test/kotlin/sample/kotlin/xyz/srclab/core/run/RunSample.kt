package sample.kotlin.xyz.srclab.core.run

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.common.lang.Current.sleep
import xyz.srclab.common.lang.IntRef.Companion.withRef
import xyz.srclab.common.run.*
import xyz.srclab.common.run.RunningContext.Companion.current
import java.time.Duration
import java.util.concurrent.CountDownLatch

class RunSample {

    @Test
    fun testRunner() {
        val runner: Runner = Runner.SYNC_RUNNER
        val intRef = 0.withRef()
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
        val scheduler = Scheduler.DEFAULT_THREAD_SCHEDULER
        val intRef = 0.withRef()
        val scheduling: Scheduling<*> = scheduler.scheduleFixedDelay<Any?>(Duration.ZERO, Duration.ofMillis(1000)) {
            intRef.set(intRef.get() + 100)
            null
        }
        sleep(2500)
        scheduling.cancel(false)
        //300
        logger.log("int: {}", intRef.get())
    }

    @Test
    @Throws(Exception::class)
    fun testRunContext() {
        val runContext = current()
        runContext.set("1", "666")
        Assert.assertEquals("666", runContext.get("1"))
        val attach = runContext.attach()
        val countDownLatch = CountDownLatch(1)
        AsyncRunner.run {
            val detach = current()
            detach.detach(attach)
            Assert.assertEquals("666", detach.get("1"))
            countDownLatch.countDown()
        }
        countDownLatch.await()
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}