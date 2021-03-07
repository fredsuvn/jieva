package sample.kotlin.xyz.srclab.common.bus

import org.testng.annotations.Test
import xyz.srclab.common.bus.EventBus
import xyz.srclab.common.bus.EventHandler
import xyz.srclab.common.bus.EventHandlerNotFoundException
import xyz.srclab.common.test.TestLogger

class EventBusSample {

    @Test
    fun testEventBus() {
        val eventBus = EventBus.newEventBus(
            listOf(
                object : EventHandler<Any> {

                    override val eventType: Any
                        get() {
                            return String::class.java
                        }

                    override fun handle(event: Any) {
                        logger.log(event)
                    }
                },
                object : EventHandler<Any> {

                    override val eventType: Any
                        get() {
                            return Int::class.java
                        }

                    override fun handle(event: Any) {
                        logger.log(event)
                    }
                }
            ))
        //1
        eventBus.emit(1)
        //2
        eventBus.emit("2")
        //No output
        eventBus.emit(Any())
        try {
            eventBus.emitOrThrow(Any())
        } catch (e: EventHandlerNotFoundException) {
            //xyz.srclab.common.bus.EventHandlerNotFoundException: class java.lang.Object
            logger.log(e)
        }
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}