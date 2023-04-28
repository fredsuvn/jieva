package xyz.srclab.common.data.json

import com.fasterxml.jackson.databind.json.JsonMapper
import xyz.srclab.common.data.DataParser
import xyz.srclab.common.data.jackson.setCommon
import xyz.srclab.common.data.jackson.toJsonSerializer

/**
 * [DataParser] for [Json].
 *
 * @see Json
 * @see DataParser
 */
interface JsonParser : DataParser<Json> {

    fun toJsonString(obj: Any?): String {
        return toString(obj)
    }

    fun toJsonBytes(obj: Any?): ByteArray {
        return toBytes(obj)
    }

    fun toJson(obj: Any?): Json {
        return toDataElement(obj)
    }

    companion object {

        private var defaultParser: JsonParser =
            JsonMapper().setCommon().toJsonSerializer()

        @JvmStatic
        fun defaultParser(): JsonParser {
            return defaultParser
        }

        @JvmStatic
        fun setDefaultParser(defaultSerializer: JsonParser) {
            this.defaultParser = defaultSerializer
        }
    }
}