package org.ttlzmc.core.api

import java.io.File
import javafx.scene.image.ImageView
import org.json.JSONObject
import org.ttlzmc.core.mod.ModInfo

/**
 * A simple wrapper for `api.modrinth.com`'s response
 */
class ModrinthMod(apiResponse: JSONObject, private val info: ModInfo) {
    val name = apiResponse.getString("title")
    val modrinthId = apiResponse.getString("id")
    val slug = apiResponse.getString("slug")

    val description = apiResponse.getString("description")

    val imageView: ImageView = ImageView(ModrinthAPIProvider.getProjectIcon(info))

    lateinit var latestVersion: ModVersion

    fun fetchLatestVersion() {
        this.latestVersion = ModrinthAPIProvider.getLatestVersion(info)
    }

    /**
     * Downloads this project at latest version
     */
    fun download(): File {
        return ModrinthAPIProvider.downloadProject(this)
    }

}