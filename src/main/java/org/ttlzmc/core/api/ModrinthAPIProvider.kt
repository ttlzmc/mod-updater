package org.ttlzmc.core.api

import java.io.File
import java.net.HttpURLConnection
import javafx.scene.image.Image
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import org.ttlzmc.core.ModFinder
import org.ttlzmc.core.mod.ModInfo
import java.util.logging.Logger

/**
 * An object that provides data using Modrinth API.
 * @see ModrinthAPILinksProvider
 */
object ModrinthAPIProvider {

    private val debugLogger = Logger.getLogger("ModrinthAPIProvider")

    fun getProject(info: ModInfo): ModrinthMod {
        return ModrinthMod(getProjectRaw(info), info)
    }

    fun getProjectRaw(mod: ModInfo): JSONObject {
        val url = ModrinthAPILinksProvider.getProject(mod)
        debugLogger.info("""
            Entrypoint: GetProject, #getProjectRaw
            URL: ${url.toExternalForm()}
        """.trimIndent())
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 6000
        connection.readTimeout = 6000
        connection.connect()
        if (connection.responseCode in 200..299) {
            return JSONObject(connection.inputStream.bufferedReader().readText())
        }
        throw ModrinthAPIResponseException("Error getting project info for ${mod.name}, \n ${connection.responseCode} : ${connection.responseMessage}")
    }

    fun getProjectIcon(mod: ModInfo): Image {
        val downloadUrl = ModrinthAPILinksProvider.getProjectIcon(mod)
        debugLogger.info("""
            Entrypoint: #getProjectIcon
            URL: ${downloadUrl.toExternalForm()}
        """.trimIndent())
        val connection = downloadUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 6000
        connection.readTimeout = 6000
        connection.connect()
        if (connection.responseCode in 200..299) {
            val temp = File.createTempFile(mod.modId, ".webp", cacheFolder())
            connection.inputStream.use { input -> temp.outputStream().use { output -> input.copyTo(output) } }
            return Image(connection.inputStream, 100.0, 100.0, true, true)
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