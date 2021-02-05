package xyz.srclab.common.id

import cn.com.essence.galaxy.annotation.Nullable
import java.lang.IllegalArgumentException
import java.util.function.BiFunction

/**
 * @author sunqian
 */
internal object IdGeneratorSpecParser {
    fun parse(
        specification: String,
        generatorFactories: Map<String?, BiFunction<String?, String?, IdComponent<*>?>?>?
    ): Array<IdComponent<*>> {
        val result: MutableList<IdComponent<*>?> = LinkedList<IdComponent<*>>()
        var serial = 0
        var startIndex = 0
        while (true) {
            val paramLeftIndex = specification.indexOf("{", startIndex)
            if (paramLeftIndex < 0) {
                result.add(ConstantComponentGenerator(specification.substring(startIndex)))
                break
            }
            if (paramLeftIndex - startIndex > 0) {
                result.add(ConstantComponentGenerator(specification.substring(startIndex, paramLeftIndex)))
            }
            val paramRightIndex = specification.indexOf("}", paramLeftIndex + 1)
            require(paramRightIndex >= 0) { "Lack \"}\" start from index: $paramLeftIndex" }
            if (paramRightIndex == paramLeftIndex + 1) {
                val realParamRightIndex = specification.indexOf("}", paramRightIndex + 1)
                require(realParamRightIndex == paramRightIndex + 1) { "Error escape, lack \"}\" start from index: $paramLeftIndex" }
                result.add(ConstantComponentGenerator("}"))
                break
            }
            result.add(
                parseComponentGenerator(
                    serial, specification.substring(paramLeftIndex + 1, paramRightIndex), generatorFactories
                )
            )
            startIndex = paramRightIndex + 1
            if (startIndex >= specification.length) {
                break
            }
            serial++
        }
        return result.toTypedArray()!!
    }

    private fun parseComponentGenerator(
        serial: Int,
        specification: String,
        generatorFactories: Map<String?, BiFunction<String?, String?, IdComponent<*>?>?>?
    ): IdComponent<*>? {
        if (specification == "{") {
            return ConstantComponentGenerator("{")
        }
        val colonIndex = specification.indexOf(":")
        if (colonIndex < 0) {
            val equalIndex = specification.indexOf("=")
            if (equalIndex < 0) {
                return generate(specification, serial.toString(), "", generatorFactories)
            }
            val type = specification.substring(0, equalIndex)
            val spec = specification.substring(equalIndex + 1)
            return generate(type, serial.toString(), spec, generatorFactories)
        }
        val name = specification.substring(0, colonIndex)
        val equalIndex = specification.indexOf("=", colonIndex + 1)
        if (equalIndex < 0) {
            val type = specification.substring(colonIndex + 1)
            return generate(type, name, "", generatorFactories)
        }
        val type = specification.substring(colonIndex + 1, equalIndex)
        val spec = specification.substring(equalIndex + 1)
        return generate(type, name, spec, generatorFactories)
    }

    private fun generate(
        type: String, name: String, spec: String,
        generatorFactories: Map<String?, BiFunction<String?, String?, IdComponent<*>?>?>?
    ): IdComponent<*>? {
        @Nullable val function = generatorFactories!![type]
            ?: throw IllegalArgumentException("ComponentGenerator not found: $type")
        return function.apply(name, spec)
    }
}