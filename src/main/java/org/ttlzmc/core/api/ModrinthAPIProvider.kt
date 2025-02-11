package org.ttlzmc.core.api

import javafx.scene.image.Image
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import org.ttlzmc.core.ModFinder
import org.ttlzmc.core.mod.ModInfo
import java.io.File
import java.net.HttpURLConnection

/**
 * An object that provides data using Modrinth API.
 * @see ModrinthAPILinksProvider
 */
object ModrinthAPIProvider {

    fun getProject(info: ModInfo): ModrinthMod {
        return ModrinthMod(getProjectRaw(info), info)
    }

    fun getProjectRaw(mod: ModInfo): JSONObject {
        val url = ModrinthAPILinksProvider.getProject(mod.name)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 6000
        connection.readTimeout = 6000
        connection.connect()
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            return JSONObject(connection.inputStream.bufferedReader().readText())
        }
        throw ModrinthAPIResponseException("Error getting project info for ${mod.name}")
    }

    fun getProjectIcon(mod: ModInfo): Image {
        val downloadUrl = ModrinthAPILinksProvider.getProjectIcon(mod)
        val connection = downloadUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 6000
        connection.readTimeout = 6000
        connection.connect()
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val temp = File.createTempFile(mod.modId, ".webp", cacheFolder())
            connection.inputStream.use { input -> temp.outputStream().use { output -> input.copyTo(output) } }
            return Image(temp.path)
        }
        throw ModrinthAPIResponseException("Error getting project icon for ${mod.name}")
    }

    fun getLatestVersion(mod: ModInfo): ModVersion {
        return getVersions(mod).first()
    }

    fun downloadProject(mod: ModrinthMod): File {
        mod.fetchLatestVersion()
        val connection = mod.latestVersion.dowloadLink.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 6000
        connection.readTimeout = 6000
        connection.connect()
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val jar = File(cacheFolder(), mod.slug + mod.latestVersion.base62version + ".jar")
            connection.inputStream.use { input -> jar.outputStream().use { output -> input.copyTo(output) } }
            return jar
        }
        throw ModrinthAPIResponseException("Error getting project icon for ${mod.name}")
    }

    private fun getVersions(mod: ModInfo): List<ModVersion> {
        val url = ModrinthAPILinksProvider.listProjectVersions(mod)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.addRequestProperty("loaders", ModFinder.foundMainLoader.key)
        connection.addRequestProperty("game_versions", ModFinder.selectedMinecraftVersion.value)
        connection.connectTimeout = 6000
        connection.readTimeout = 6000
        connection.connect()
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val versions = JSONArray(JSONTokener(connection.inputStream.bufferedReader().readText()))
            return versions.map { ModVersion(it as JSONObject) }.toList()
        }
        throw ModrinthAPIResponseException("Error getting project versions for mod ${mod.name}")
    }

    private fun cacheFolder(): File {
        val homeDir = System.getProperty("user.home")
        val updaterCacheDir = File(homeDir, "UpdaterCache")
        if (!updaterCacheDir.exists()) updaterCacheDir.mkdirs()
        return updaterCacheDir
    }

}