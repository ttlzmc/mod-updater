package org.ttlzmc.core.api

import com.google.gson.JsonObject
import java.io.File
import javafx.scene.image.ImageView
import org.ttlzmc.core.UpdaterCache
import org.ttlzmc.core.mod.ModInfo

/**
 * A simple wrapper for `api.modrinth.com`'s response
 */
class ModrinthMod(apiResponse: JsonObject, private val info: ModInfo) {
    val name = apiResponse.get("title").asString
    val modrinthId = apiResponse.get("id").asString
    val slug = apiResponse.get("slug").asString

    val description = apiResponse.get("description").asString

    val imageView: ImageView = UpdaterCache.loadIcon("${name}.png")

    lateinit var latestVersion: ModVersion

    fun fetchLatestVersion() {
        this.latestVersion = ModrinthAPIProvider.getLatestVersion(info)
    }

    /**
     * Downloads this project's latest version
     */
    fun download(): File {
        return ModrinthAPIProvider.downloadProject(this)
    }

}