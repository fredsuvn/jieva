package xyz.srclab.common.serialize.json

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.typeRef
import xyz.srclab.common.serialize.Serial

/**
 * Represents a `Json Serial`, can fast convert between literal json and java object.
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
     * Returns stringified value of this [Json].
     * If content is `abs`, returns "abs".
     *
     * @see [toObjectString]
     */
    fun toJsonString(): String

    /**
     * Returns stringified value of this [Json].
     * If content is `abs`, returns "abs"'s bytes.
     *
     * @see [toObjectString]
     */
    fun toJsonBytes(): ByteArray

    /**
     * Converts this [Json] to [String].
     * If content is `abs`, returns abs.
     *
     * @see [toJsonString]
     */
    fun toObjectString(): String {
        return toObject(String::class.java)
    }

    /**
     * Same as [toJsonString].
     */
    override fun toString(): String

    /**
     * Returns as [Map]<[String], [Json]>.
     */
    @JvmDefault
    fun toJsonMap(): Map<String, Json> {
        return toObject(typeRef())
    }
}