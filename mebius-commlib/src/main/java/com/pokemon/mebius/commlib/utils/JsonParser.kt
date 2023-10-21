package com.pokemon.mebius.commlib.utils

import java.lang.reflect.Type

/**
 * 不依赖gson等序列化框架，通过外部依赖注入的方式
 */
interface JsonParser {
    fun toJson(src: Any): String

    fun <T> fromJson(json: String, clazz: Class<T>): T

    fun <T> fromJson(json: String, typeOfT: Type): T
}

object CommJsonParser {
    private var jsonParser: JsonParser? = null

    fun initJsonParser(jsonParserP: JsonParser) {
        jsonParser = jsonParserP
    }

    fun getCommJsonParser(): JsonParser {
        if (jsonParser == null) {
            throw IllegalStateException("you must init CommJsonParser first")
        }
        return jsonParser!!
    }

}