package org.ttlzmc.core

import com.google.gson.JsonParser
import java.util.LinkedList

object MinecraftVersions {

    private val versionsJSON = JsonParser.parseString(javaClass.classLoader.getResourceAsStream("versions.json").bufferedReader().readText()).asJsonArray
    private val versions: LinkedList<MinecraftVersion> = LinkedList()

    fun load() {
        versionsJSON.forEach { versions.add(MinecraftVersion(it.asString)) }
    }

    fun entries() = versions

    data class MinecraftVersion(val value: String)
}