package xyz.srclab.common.net

import xyz.srclab.common.base.currentMillis
import java.nio.channels.ClosedSelectorException
import java.nio.channels.SelectionKey
import java.nio.channels.Selector

/**
 * Enhanced [Selector] which avoids some JVM bug (such as high CPU caused by select 0).
 */
interface NetSelector {

    /**
     * Returns next [SelectionKey], this is **not thread safe**.
     */
    @Throws(ClosedSelectorException::class)
    fun next(): SelectionKey

    /**
     * Closes this selector, this is **thread safe**.
     */
    fun close()

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun Selector.toNetSelector(emptySelectThreshold: Int = 256): NetSelector {
            return NetSelectorImpl(this, emptySelectThreshold)
        }

        private class NetSelectorImpl(
            private var selector: Selector,
            private val emptySelectThreshold: Int
        ) : NetSelector {

            private var keyIterator: MutableIterator<SelectionKey> = emptyKeyIterator
            private var lastEmptySelectTime: Long = 0L
            private var emptySelectCount: Int = 0

            private var isClose = false

            override fun next(): SelectionKey {
                if (keyIterator.hasNext()) {
                    val next = keyIterator.next()
                    next.channel()
                    keyIterator.remove()
                    println("next>>>>>>>> $next")
                    return next
                }
                var selectCount = selector.select()
                println("selectCount>>>>>>>>> $selectCount")
                while (selectCount == 0) {
                    val now = currentMillis()
                    if (now == lastEmptySelectTime) {
                        emptySelectCount++
                        if (emptySelectCount >= emptySelectThreshold) {
                            //High CPU if select count is always 0
                            emptySelectCount = 0
                            rebuildSelector()
                            return next()
                        } else {
                            selectCount = selector.select()
                        }
                    } else {
                        lastEmptySelectTime = now
                        emptySelectCount = 0
                        selectCount = selector.select()
                    }
                }
                val selectedKeys = selector.selectedKeys()
                keyIterator = selectedKeys.iterator()
                return next()
            }

            private fun rebuildSelector() {
                val newSelector = Selector.open()
                for (key in selector.keys()) {
                    if (!key.isValid || key.interestOps() == 0) {
                        continue
                    }
                    key.cancel()
                    val channel = key.channel()
                    val attachment = key.attachment()
                    channel.register(newSelector, key.interestOps(), attachment)
                    selector.selectNow()
                }
                synchronized(this) {
                    selector.close()
                    selector = newSelector
                    if (isClose) {
                        newSelector.close()
                    }
                }
            }

            override fun close() {
                synchronized(this) {
                    selector.close()
                    isClose = true
                }
            }

            companion object {
                private val emptyKeyIterator: MutableIterator<SelectionKey> = object : MutableIterator<SelectionKey> {
                    override fun hasNext(): Boolean = false
                    override fun next(): SelectionKey = throw NoSuchElementException("This iterator is empty!")
                    override fun remove() = throw IllegalStateException("This iterator is empty!")
                }
            }
        }
    }
}