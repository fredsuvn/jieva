package sample.kotlin.xyz.srclab.core.bus

import org.testng.annotations.Test
import xyz.srclab.common.bus.EventBus
import xyz.srclab.common.bus.Subscribe
import xyz.srclab.common.lang.Next
import xyz.srclab.common.test.TestLogger

class EventBusSample {

    @Test
    fun testEventBus() {
        val eventBus = EventBus.newEventBus()
        val handler1 = Handler1()
        eventBus.register(handler1)
        eventBus.post("123")
        //sub3sub2sub0 or sub0sub3sub2
        logger.log("subs: " + handler1.stack)
        eventBus.unregister(handler1)
    }

    class Handler1 {

        var stack = ""

        @Subscribe(priority = 100)
        fun sub0(chars: CharSequence) {
            logger.log("sub0:$chars")
            stack += "sub0"
        }

        @Subscribe
        fun sub1(chars: String) {
            logger.log("sub1:$chars")
            stack += "sub1"
        }

        @Subscribe(priority = 100)
        fun sub2(chars: String): Next {
            logger.log("sub2:$chars")
            stack += "sub2"
            return Next.BREAK
        }

        @Subscribe(priority = 200)
        fun sub3(chars: String) {
            logger.log("sub3:$chars")
            stack += "sub3"
        }
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}