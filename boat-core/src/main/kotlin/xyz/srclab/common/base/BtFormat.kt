package xyz.srclab.common.base

/**
 * Formats using slf4j-like style (not exactly same):
 *
 * ```
 * fastFormat("1 + 1 = {}, 2 + 2 = {}", 2, 4);
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
 * format("C:\\\\{}\\_\\{}", "file.zip");
 * ```
 *
 * will output:
 *
 * ```
 * C:\file.zip\_{}
 * ```
 *
 * If Args' size less than `{}`, will output `{}` itself:
 *
 * ```
 * format("C:\\\\{}\\_{}", "file.zip");
 * ```
 *
 * will output:
 *
 * ```
 * C:\file.zip\_{}
 * ```
 */
fun fastFormat(pattern: CharSequence, vararg args: Any?): String {
    if (pattern.isEmpty()) {
        return pattern.toString()
    }

    var buffer: CharsBuilder? = null
    fun getBuffer(): CharsBuilder {
        val bf = buffer
        if (bf === null) {
            val newBuffer = CharsBuilder()
            buffer = newBuffer
            return newBuffer
        }
        return bf
    }

    var start = 0
    var i = 0
    var argIndex = 0
    while (i < pattern.length) {
        val c = pattern[i]
        if (c == '\\') {
            i++
            if (i >= pattern.length) {
                break
            }
            val cn = pattern[i]
            if (cn == '\\') {
                //Escape: \\ -> \
                getBuffer().append(pattern.subRef(start, i))
                i++
                start = i
                continue
            }
            if (cn == '{') {
                i++
                if (i > pattern.length) {
                    break
                }
                val cnn = pattern[i]
                if (cnn == '}') {
                    //Escape: \{} -> {}
                    getBuffer().append(pattern.subRef(start, i - 2))
                    start = i - 1
                    i++
                    continue
                }
            }
        }
        if (c == '{') {
            i++
            if (i >= pattern.length) {
                break
            }
            val cn = pattern[i]
            if (cn == '}') {
                //Insert parameter
                if (!argIndex.isInBounds(0, args.size)) {
                    //Args bounds out of bounds
                    //throw StringFormatException("Argument index out of bounds: $argIndex")
                    i++
                    continue
                }
                getBuffer().append(pattern.subRef(start, i - 1))
                getBuffer().append(args[argIndex])
                i++
                start = i
                argIndex++
                continue
            }
        }
        i++
    }

    if (buffer === null) {
        return pattern.toString()
    }
    if (start < pattern.length) {
        getBuffer().append(pattern.subRef(start))
    }

    return getBuffer().toString()
}

/**
 * Formats string with [pattern] and [args] by `String.format` (printf() style).
 */
fun format(pattern: CharSequence, vararg args: Any?): String {
    return JavaString.format(pattern.toString(), *args)
}