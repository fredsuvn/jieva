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
 * Opens a new [Selector] and registers all keys of old [Selector] to the new [Selector].
 * Old keys will be canceled and old selector will be closed.
 */
@JvmName("copyAndCloseSelector")
fun Selector.copyAndClose(): Selector {
    val newSelector = Selector.open()
    for (key in this.keys()) {
        key.cancel()
        key.channel().register(newSelector, key.interestOps(), key.attachment())
    }
    this.selectNow()
    return newSelector
}