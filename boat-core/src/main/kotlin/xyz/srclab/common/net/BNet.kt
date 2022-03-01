@file:JvmName("BNet")

package xyz.srclab.common.net

import java.nio.channels.ClosedSelectorException
import java.nio.channels.SelectionKey
import java.nio.channels.Selector


fun nioListen(selector: Selector, handler: (SelectionKey) -> Unit) {
    while (true) {
        try {
            val readyChannels = selector.select()
            if (readyChannels == 0) {
                continue
            }
            val selectedKeys = selector.selectedKeys()
            val iterator = selectedKeys.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                handler(key)
                iterator.remove();
            }
        } catch (e: ClosedSelectorException) {
            break
        }
    }
}

/**
 * Opens a new [Selector] and move all registered ops from [this] to the new [Selector].
 */
@JvmName("reopenSelector")
fun Selector.reopen(): Selector {
    val newSelector = Selector.open()
    for (key in this.keys()) {
        key.cancel()
        key.channel().register(newSelector, key.interestOps(), key.attachment())
    }
    this.selectNow()
    return newSelector
}