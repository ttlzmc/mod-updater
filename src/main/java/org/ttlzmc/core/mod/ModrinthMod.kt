package org.ttlzmc.core.mod

import com.google.gson.JsonObject
import org.ttlzmc.core.api.LabrinthAPIProvider
import org.ttlzmc.utils.getString
import java.net.URL

/**
 * A simple wrapper for `api.modrinth.com/project/`'s response
 */
class ModrinthMod(
    val apiResponse: JsonObject,
    val version: ModVersion,
    val loader: Loader,
    private val hash: String,
) {

    val name: String = apiResponse.getString("title")
    val id: String = apiResponse.getString("id")

    val description: String = apiResponse.getString("description")
    //val icon: ImageView = UpdaterCache.loadIcon("${name}.png")

    val downloadLink: URL = version.downloadLink

    val minecraftVersion: String = apiResponse.getAsJsonArray("game_versions").last().asString

    private lateinit var latestVersion: ModVersion

    fun jarSHA512(): String {
        return this.hash
    }

    fun fetchLatestVersion() {
        this.latestVersion = LabrinthAPIProvider.getLatestVersion(this, hash)
    }

    fun latestVersion(): ModVersion {
        return latestVersion
    }

}