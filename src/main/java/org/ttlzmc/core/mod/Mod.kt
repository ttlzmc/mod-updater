package org.ttlzmc.core.mod

import org.json.JSONObject

class Mod(info: ModInfo) {

    private var modInfo: JSONObject = JSONObject(mapOf(
        "modId" to info.modId,
        "name" to info.name,
        "description" to info.description,
        "version" to info.version
    ))

    val modId: String = modInfo.getString("modId")
    val name: String = modInfo.getString("name")
    val description: String = modInfo.getString("description")

    val currentVersion: String = modInfo.getString("version")

    val loader: Loader = info.loader

    lateinit var modrinthProjectData: JSONObject

    fun fetchModrinthData() {

    }

}