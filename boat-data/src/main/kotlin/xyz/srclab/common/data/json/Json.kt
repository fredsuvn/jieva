package xyz.srclab.common.data.json

import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.data.Serial

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
     * To json string.
     *
     * Note if content is `abs`, returns `"abs"`.
     *
     * @see [toText]
     */
    fun toJsonString(): String

    /**
     * To json string as bytes.
     *
     * Note if content is `abs`, returns bytes of `"abs"`.
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

    /**
     * Returns as [Array]<[Json]>.
     */
    fun parseJsonArray(): Array<Json> {
        return parse(Array<Json>::class.java)
    }
}