package xyz.srclab.common.serialize.json

import com.fasterxml.jackson.databind.node.NullNode
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.serialize.Serial
import java.lang.reflect.Type

/**
 * Represents a Json, fast convert between literal json and java object.
 *
 * @author sunqian
 */
interface Json : Serial {

    /**
     * Returns type of json.
     *
     * @return type of json
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: JsonType
        @JvmName("type") get

    /**
     * to json string. If you want to java [String], use toObject(String.class).
     *
     * @return json string
     */
    override fun toString(): String

    /**
     * to T
     *
     * @param type type of T
     * @param [T]  java type
     * @return [T]
     */
    @JvmDefault
    override fun <T> toObject(type: Type): T {
        return toObjectOrNull<T>(type).orThrow()
    }

    companion object {

        @JvmField
        val NULL: Json = JsonImpl(DEFAULT_OBJECT_MAPPER, NullNode.getInstance())

        private fun <T> T?.orThrow(): T {
            return this ?: throw IllegalStateException("Null json node, use toObjectOrNull.")
        }
    }
}