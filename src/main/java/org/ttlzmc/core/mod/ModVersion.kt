package org.ttlzmc.core.mod

import com.google.gson.JsonObject
import org.ttlzmc.core.MinecraftVersions.MinecraftVersion
import org.ttlzmc.utils.getString
import java.net.URI
import java.net.URL

/**
 * A simple wrapper for `api.modrinth.com/project/version`'s response
 */
class ModVersion(apiResponse: JsonObject) {
    val projectId = apiResponse.getString("project_id")

    val base62version = apiResponse.getString("id")
    val versionNumber = apiResponse.getString("version_number")

    val minecraftVersions: List<MinecraftVersion> = apiResponse.getAsJsonArray("game_versions").map { MinecraftVersion(it.asString) }
    val loaders: List<Loader> = apiResponse.getAsJsonArray("loaders").map { loader -> Loader.entries.first { it.name == loader.asString.uppercase() } }

    val downloadLink: URL = URI.create(apiResponse.get("files")
        .asJsonArray.get(0).asJsonObject.getString("url")).toURL()
}