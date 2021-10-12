package xyz.srclab.common.serialize.json

import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.serialize.Serial

/**
 * Represents a `Json [Serial]`, middle type between raw json string/bytes and java object.
 *
 * @author sunqian
 */
interface Json : Serial {

    /**
     * Returns type of json.
     *
     * @return type of json
     */
    val type: JsonType

    /**
     * Returns stringified value of this [Json].
     * If content is `abs`, returns "abs".
     *
     * @see [toText]
     */
    fun toJsonString(): String

    /**
     * Returns stringified value of this [Json].
     * If content is `abs`, returns "abs"'s bytes.
     *
     * @see [toBytes]
     */
    fun toJsonBytes(): ByteArray

    /**
     * Returns as [Map]<[String], [Json]>.
     */
    fun parseJsonMap(): Map<String, Json> {
        return parse(object : TypeRef<Map<String, Json>>() {})
    }
}