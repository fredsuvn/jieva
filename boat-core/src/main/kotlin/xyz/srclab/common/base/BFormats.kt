@file:JvmName("BFormats")

package xyz.srclab.common.base

import xyz.srclab.common.base.StringRef.Companion.stringRef
import java.util.*

/**
 * Fast format using slf4j-like style (may not exactly same):
 *
 * ```
 * BFormats.fastFormat("1 + 1 = {}, 2 + 2 = {}", 2, 4);
 * ```
 *
 * Using `\` as escape in following cases:
 *
 * * `\{}` will be escaped as `{}`;
 * * `\\` will be escaped as `\`;
 * * Otherwise no escape;
 *
 * For example:
 *
 * ```
 * //Note java string `\\` means `\`
 * BFormats.fastFormat("C:\\\\{}\\_\\{}", "file.zip");
 * ```
 *
 * will output:
 *
 * ```
 * C:\file.zip\_{}
 * ```
 */
fun CharSequence.fastFormat(vararg args: Any?): String {
    //return MessageFormatterSlf4j.arrayFormat(this.toString(), args, null).message

    var buffer: MutableList<Any?>? = null

    fun getBuffer(): MutableList<Any?> {
        val bf = buffer
        if (bf === null) {
            val newBuffer = LinkedList<Any?>()
            buffer = newBuffer
            return newBuffer
        }
        return bf
    }

    var start = 0
    var i = 0
    var argIndex = 0
    while (i < this.length) {
        val c = this[i]
        if (c == '\\') {
            i++
            if (i < this.length) {
                val cn = this[i]
                if (cn == '\\') {
                    //Escape: \\ -> \
                    getBuffer().add(this.stringRef(start, i))
                    i++
                    start = i
                    continue
                } else if (cn == '{') {
                    i++
                    if (i < this.length) {
                        val cnn = this[i]
                        if (cnn == '}') {
                            //Escape: \{} -> {}
                            getBuffer().add(this.stringRef(start, i - 2))
                            start = i - 1
                            i++
                            continue
                        }
                    } else {
                        break
                    }
                }
            } else {
                break
            }
        } else if (c == '{') {
            i++
            if (i < this.length) {
                val cn = this[i]
                if (cn == '}') {
                    //Insert parameter
                    if (!argIndex.isIndexInBounds(0, args.size)) {
                        throw IndexOutOfBoundsException("Argument index out of bounds: $argIndex")
                    }
                    getBuffer().add(this.stringRef(start, i - 1))
                    getBuffer().add(args[argIndex])
                    i++
                    start = i
                    argIndex++
                    continue
                }
            } else {
                break
            }
        }
        i++
    }

    if (buffer === null) {
        return this.toString()
    }
    if (start < this.length) {
        getBuffer().add(this.stringRef(start))
    }

    return getBuffer().joinToString("") { it.deepToString() }
}