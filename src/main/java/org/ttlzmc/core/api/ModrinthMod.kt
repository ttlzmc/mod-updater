package org.ttlzmc.core.api

import javafx.scene.image.ImageView
import org.json.JSONObject
import org.ttlzmc.core.mod.ModInfo

/**
 * Represents a mod fetched from `api.modrinth.com`
 */
class ModrinthMod(apiResponse: JSONObject, originalFileInfo: ModInfo) {
    val name = apiResponse.getString("title")
    val modrinthId = apiResponse.getString("id")
    val slug = apiResponse.getString("slug")

    val description = apiResponse.getString("description")

    val imageView: ImageView = ImageView(ModrinthAPIProvider.getProjectIcon(originalFileInfo))

    fun fetchLatestVersion() {

    }

}