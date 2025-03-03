package org.ttlzmc.utils

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

fun JsonObject.getString(key: String): String {
    return this.get(key).asString
}

fun JsonObject.getInt(key: String): Int {
    return this.get(key).asInt
}

fun JsonObject.getStringOrElse(key: String, defaultValue: () -> String): String {
    return this.getOrElse(key) { JsonPrimitive(defaultValue()) }.asString
}

fun JsonObject.getOrElse(key: String, defaultValue: () -> JsonPrimitive): JsonElement {
    return this.get(key) ?: defaultValue()
}
