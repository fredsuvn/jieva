package xyz.srclab.common.data.json

import xyz.srclab.common.data.DataNode
import xyz.srclab.common.reflect.TypeRef

/**
 * Represents a json type.
 *
 * @see DataNode
 */
interface Json : DataNode {

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
     * @see [toString]
     */
    fun toJsonString(): String {
        return toString()
    }

    /**
     * To json string as bytes.
     *
     * Note if content is `abs`, returns bytes of `"abs"`.
     *
     * @see [toBytes]
     */
    fun toJsonBytes(): ByteArray {
        return toBytes()
    }

    /**
     * Returns as [Map]<[String], [Json]>.
     */
    fun toJsonMap(): Map<String, Json> {
        return toObject(object : TypeRef<Map<String, Json>>() {})
    }

    /**
     * Returns as [Array]<[Json]>.
     */
    fun toJsonArray(): Array<Json> {
        return toObject(Array<Json>::class.java)
    }
}