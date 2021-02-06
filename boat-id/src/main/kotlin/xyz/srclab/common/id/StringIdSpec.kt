package xyz.srclab.common.id

import xyz.srclab.common.base.toLong

/**
 * Specification implementation of [IdFactory]. For example:
 * ```
 * String spec = "seq-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d}-{Constant,tail}";
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
 * [IdComponentGenerator]s are wrapped between {}, format is {name[,param]+}.
 *
 * Using `\` to escape:
 * ```
 * seq\{\}-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d\\}-{Constant,tail}
 * will output:
 * seq{}-202007311734474130000\-tail
 * ```
 *
 * Note literal parts will be seem as [ConstantComponentGenerator]s, these two spec are same:
 * ```
 * seq-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d}-{Constant,tail}
 * seq-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d}-tail
 * ```
 *
 * There are default name of [IdComponentGenerator]:
 * * [ConstantComponentGenerator.NAME]
 * * [TimeCountComponentGenerator.NAME]
 * Using [StringIdFactoryGenerator] can extend, note generation arguments of [StringIdFactoryGenerator]
 * only support [String] type.
 *
 * @author sunqian
 *
 * @see StringIdFactoryGenerator
 */
class StringIdSpec @JvmOverloads constructor(
    spec: String,
    factoryGenerator: StringIdFactoryGenerator = StringIdFactoryGenerator.DEFAULT
) : StringIdFactory(parseIdSpec(spec, factoryGenerator))

/**
 * Help generating [StringIdFactory] by name and arguments.
 */
interface StringIdFactoryGenerator {

    fun generate(name: String, args: List<String>): IdComponentGenerator<*>

    companion object {

        @JvmField
        val DEFAULT = object : StringIdFactoryGenerator {
            override fun generate(name: String, args: List<String>): IdComponentGenerator<*> {
                return when (name) {
                    ConstantComponentGenerator.NAME -> ConstantComponentGenerator(
                        args[0]
                    )
                    TimeCountComponentGenerator.NAME -> TimeCountComponentGenerator(
                        args[0],
                        args[1].toLong(),
                        args[2]
                    )
                    else -> throw IllegalArgumentException("Id component not found: $name")
                }
            }
        }
    }
}

private fun parseIdSpec(spec: String, factoryGenerator: StringIdFactoryGenerator): List<IdComponentGenerator<*>> {

    fun doEscape(index: Int, spec: String, buffer: StringBuilder): Int {
        if (index + 1 >= spec.length) {
            throw IllegalArgumentException("Lack of escape character at index: $index")
        }
        buffer.append(spec[index + 1])
        return index + 2
    }

    fun parseGenerator(index: Int, spec: String, args: MutableList<String>): Int {
        var i = index
        val buffer = StringBuilder()
        while (i < spec.length) {
            val c = spec[i]
            if (c == '\\') {
                i = doEscape(i, spec, buffer)
                continue
            }
            if (c == ',') {
                args.add(buffer.toString())
                buffer.clear()
                i++
                continue
            }
            if (c == '}') {
                args.add(buffer.toString())
                return i + 1
            }
            buffer.append(c)
            i++
        }
        throw IllegalArgumentException("Not paired {} at index: $i")
    }

    val result = ArrayList<IdComponentGenerator<*>>()
    var index = 0
    val buffer = StringBuilder()
    while (index < spec.length) {
        val c = spec[index]
        if (c == '\\') {
            index = doEscape(index, spec, buffer)
            continue
        }
        if (c == '{') {
            if (buffer.isNotEmpty()) {
                result.add(factoryGenerator.generate(ConstantComponentGenerator.NAME, listOf(buffer.toString())))
                buffer.clear()
            }
            val args = ArrayList<String>()
            index = parseGenerator(index + 1, spec, args)
            result.add(factoryGenerator.generate(args[0], args.subList(1, args.size)))
            continue
        }
        buffer.append(c)
        index++
    }
    if (buffer.isNotEmpty()) {
        result.add(factoryGenerator.generate(ConstantComponentGenerator.NAME, listOf(buffer.toString())))
    }
    return result
}