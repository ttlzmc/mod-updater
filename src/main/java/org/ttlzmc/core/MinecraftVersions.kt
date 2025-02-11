package org.ttlzmc.core

import org.json.JSONArray
import org.json.JSONTokener
import java.util.LinkedList

object MinecraftVersions {

    private val versionsJSON = JSONArray(JSONTokener(this.javaClass.getResourceAsStream("/versions.json")))
    private val versions: LinkedList<MinecraftVersion> = LinkedList()

    fun load() {
        versionsJSON.forEach { versions.add(MinecraftVersion(it as String)) }
    }

    fun entries() = versions

    data class MinecraftVersion(val value: String)
}