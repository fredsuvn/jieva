package xyz.srclab.common.id

import xyz.srclab.common.base.toLong

/**
 * Specification implementation of [IdFactory]. For example:
 * ```
 * String spec = "seq-{t:TimestampCount(yyyyMMddHHmmssSSS,1023,%17s%04d)}-{Constant(tail)}";
 * StringIdSpec idFactory = new StringIdSpec(spec);
 * ```
 * The result may be:
 * ```
 * seq-202007311734474130000-tail
 * seq-202007311734474130001-tail
 * seq-202007311734474140000-tail
 * seq-202007311734474140001-tail
 * ```
 * An [StringIdSpec] consists of literals and [IdComponentGenerator]s,
 * Components are wrapped between {}, format is {[name:]type(params)}. This spec info will be
 * parsed to an [List], its first argument is name, second is type, and next are rests in defined order.
 *
 * Using {} to escape `{`, `}`, `(`, `)`, `:` and `,`:
 * ```
 * seq{{}{}}-{t:TimestampCount(yyyyMMddHHmmssSSS,1023,%17s%04d{(}{)})}-{Constant(tail)}
 * will output:
 * seq{}-202007311734474130000()-tail
 * ```
 *
 * Note literal parts will be seem as [ConstantComponentGenerator]s, their default names are number-counting start
 * from 0, don't set conflict name for your custom [IdComponentGenerator].
 *
 * @author sunqian
 */
class StringIdSpec @JvmOverloads constructor(
    spec: String,
    specFunction: (args: List<Any?>) -> IdComponentGenerator<*> = DEFAULT_SPEC_FUNCTION
) : StringIdFactory(parseSpec(spec).map { specFunction(it) })

internal val DEFAULT_SPEC_FUNCTION: (args: List<Any?>) -> IdComponentGenerator<*> = { arguments ->
    when (val type = arguments[0]) {
        ConstantComponentGenerator.TYPE -> ConstantComponentGenerator(
            arguments[1].toString(),
            arguments[2]
        )
        TimestampCountComponentGenerator.TYPE -> TimestampCountComponentGenerator(
            arguments[1].toString(),
            arguments[2]?.toString(),
            arguments[3]!!.toLong(),
            arguments[4]?.toString()
        )
        else -> throw IllegalArgumentException("Id component type not found: $type")
    }
}

private fun parseSpec(spec: String): List<List<Any?>> {

    fun parseGeneratorArgs(result: ArrayList<List<Any?>>, spec: String, index: Int, name: String):Int {

    }

    fun parseGenerator(result: ArrayList<List<Any?>>, spec: String, index: Int, count: Int): Int {
        var name:String? = null
        var i = index
        val buffer = StringBuilder()
        while (i < spec.length) {
            val c = spec[i]
            if (c == '{') {
                if (i + 2 >= spec.length) {
                    throw IllegalArgumentException("Lack of '}' at index: $i")
                }
                if (spec[i + 2] == '}') {
                    buffer.append(spec[i + 1])
                    i += 3
                    continue
                }
                throw IllegalArgumentException("Nesting id component generator definition at index: $i")
            }
            if (c == ':'){
                if (name !== null) {
                    throw IllegalArgumentException("Repeating id component generator name definition at index: $i")
                }
                name = buffer.toString()
                i++
                continue
            }
            if (c == '(') {
                return parseGeneratorArgs(result, spec, i+1,name?:count.toString())
            }
        }
    }

    val result = ArrayList<List<Any?>>()
    var count = 0
    var index = 0
    val buffer = StringBuilder()
    while (index < spec.length) {
        val c = spec[index]
        if (c == '{') {
            if (index + 2 >= spec.length) {
                throw IllegalArgumentException("Lack of '}' at index: $index")
            }
            if (spec[index + 2] == '}') {
                buffer.append(spec[index + 1])
                index += 3
                continue
            }
            if (buffer.isNotEmpty()) {
                result.add(listOf(ConstantComponentGenerator.TYPE, count++.toString(), buffer.toString()))
                buffer.clear()
            }
            index = parseGenerator(result, spec, index + 1, count++)
            continue
        }
        buffer.append(c)
        index++
    }
    if (buffer.isNotEmpty()) {
        result.add(listOf(ConstantComponentGenerator.TYPE, count.toString(), buffer.toString()))
        buffer.clear()
    }
    return result
}