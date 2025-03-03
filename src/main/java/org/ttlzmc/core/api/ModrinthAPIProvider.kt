package org.ttlzmc.core.api

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.net.HttpURLConnection
import org.ttlzmc.core.ModFinder
import org.ttlzmc.core.UpdaterCache
import org.ttlzmc.core.UpdaterCache.cacheFolder
import org.ttlzmc.core.mod.ModInfo
import org.ttlzmc.utils.getString
import java.util.logging.Logger

/**
 * An object that provides data using Modrinth API.
 * @see ModrinthAPILinksProvider
 */
object ModrinthAPIProvider {

    private val debugLogger = Logger.getLogger("ModrinthAPIProvider")

    fun getProject(info: ModInfo): ModrinthMod {
        val apiResponse = fetchProjectInfo(info)
        //this.downloadProjectIcon(apiResponse)
        return ModrinthMod(apiResponse, info)
    }

    fun fetchProjectInfo(mod: ModInfo): JsonObject {
        val url = ModrinthAPILinksProvider.getProject(mod)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 6000
        connection.readTimeout = 6000
        connection.connect()
        if (connection.responseCode in 200..299) {
            debugLogger.info("Mod found successfully: ${url.toExternalForm()}")
            return JsonParser.parseString(connection.inputStream.bufferedReader().readText()).asJsonObject
        }
        debugLogger.info("Error getting project info for ${mod.name}, ${mod.modId} \n ${connection.responseCode} : ${connection.responseMessage}")
        throw ModrinthAPIResponseException("Error getting project info for ${mod.name}")
    }

    fun downloadProjectIcon(modInfo: JsonObject) {
        val downloadUrl = ModrinthAPILinksProvider.getProjectIcon(modInfo)
        debugLogger.info("GetProjectIcon: ${downloadUrl.toExternalForm()}, $modInfo")
        val connection = downloadUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 6000
        connection.readTimeout = 6000
        connection.connect()
        if (connection.responseCode in 200..299) {
            UpdaterCache.cacheIcon("${modInfo.getString("title")}.webp", connection.inputStream)
        }
        throw ModrinthAPIResponseException("Error getting project icon for ${modInfo.getString("title")}")
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
        if (connection.responseCode in 200..299) {
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
            val versions = JsonParser.parseString(connection.inputStream.bufferedReader().readText()).asJsonArray
            return versions.map { ModVersion(it as JsonObject) }.toList()
        }
        throw ModrinthAPIResponseException("Error getting project versions for mod ${mod.name}")
    }

}