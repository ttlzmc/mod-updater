package org.ttlzmc.core.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.ttlzmc.core.mod.ModInfo
import org.ttlzmc.core.mod.ModVersion
import org.ttlzmc.core.mod.ModrinthMod
import java.io.File
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URI
import java.security.MessageDigest
import java.util.logging.Logger

/**
 * An object that provides necessary projects-related data using Modrinth API.
 *
 * Labrinth official docs: [https://docs.modrinth.com/api/](https://docs.modrinth.com/api/)
 */
object LabrinthAPIProvider {

    private val debugLogger = Logger.getLogger("LabrinthAPIProvider")

    /**
     * Gets full project info for selected `minecraftVersion` and `loader`.
     *
     * The version of the mod will depend on the haѕh obtained from the jar file.
     * @see ModrinthMod
     * @see ModVersion
     */
    fun getProject(modInfo: ModInfo): ModrinthMod {
        val hash = hashSHA512(modInfo.jarFile)
        val version = ModVersion(this.getVersion(modInfo.name, hash))
        val project = this.fetchProjectInfo(version)
        return ModrinthMod(project, version, modInfo.loader, hash)
    }

    /**
     * Gets raw project's json, containing all project info.
     * @see ModrinthMod
     */
    private fun fetchProjectInfo(version: ModVersion): JsonObject {
        val url = URI.create("https://api.modrinth.com/v2/project/${version.projectId}").toURL()
        val connection = (url.openConnection() as HttpURLConnection).apply {
            setRequestProperty("Accept", "application/json")
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
        }.also { it.connect() }
        if (connection.responseCode in 200..299) {
            this.debugLogger.info("Mod found successfully: ${url.toExternalForm()}")
            return JsonParser.parseString(connection.inputStream.bufferedReader().readText()).asJsonObject
        }
        this.debugLogger.info("Error fetching project info for mod: ${version.projectId} (${url.toExternalForm()})")
        throw ModrinthAPIResponseException("Error fetching project info for mod: ${version.projectId} (${url.toExternalForm()})")
    }

    /**
     * Gets project version, based on given jar file hash.
     * @see ModVersion
     */
    private fun getVersion(fallbackModName: String, hash: String): JsonObject {
        val url = URI.create("https://api.modrinth.com/v2/version_file/$hash").toURL()
        val connection = (url.openConnection() as HttpURLConnection).apply {
            setRequestProperty("algorithm", "sha512")
            setRequestProperty("Accept", "application/json")
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
        }.also { it.connect() }
        if (connection.responseCode in 200..299) {
            this.debugLogger.info("Mod version found successfully: ${url.toExternalForm()}")
            return JsonParser.parseString(connection.inputStream.bufferedReader().readText()).asJsonObject
        }
        this.debugLogger.info("Error getting version info for mod: ${url.toExternalForm()} (${fallbackModName})")
        throw ModrinthAPIResponseException("Error getting version info for mod: ${url.toExternalForm()} (${fallbackModName})")
    }

    /**
     * Gets latest project version, based on given jar file hash.
     * @see ModVersion
     */
    fun getLatestVersion(mod: ModrinthMod, hash: String): ModVersion {
        val url = URI.create("https://api.modrinth.com/v2/version_file/$hash/update").toURL()
        val connection = (url.openConnection() as HttpURLConnection).apply {
            addRequestProperty("algorithm", "sha512")
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
            requestMethod = "POST"
            connectTimeout = 6000
            readTimeout = 6000
            doOutput = true
            val body = JsonObject().apply {
                add("loaders", JsonArray().apply { add(mod.loader.key) })
                add("game_versions", JsonArray().apply { add(mod.minecraftVersion) })
            }
            outputStream.write(body.asString.toByteArray())
        }.also { it.connect() }
        if (connection.responseCode in 200..299) {
            val version = JsonParser.parseString(connection.inputStream.bufferedReader().readText()).asJsonObject
            return ModVersion(version)
        }
        throw ModrinthAPIResponseException("Error getting latest version for mod: $hash")
    }

    private fun hashSHA512(file: File): String {
        if (!file.exists() || !file.canRead()) {
            debugLogger.info("Cannot hash file ${file.absolutePath} because it doesn't exist or unable to be read.")
            throw FileNotFoundException()
        }

        return try {
            val digest = MessageDigest.getInstance("SHA-512")
            val fileBytes = file.readBytes()
            val hashBytes = digest.digest(fileBytes)
            hashBytes.joinToString("") { String.format("%02x", it) }
        } catch (e: Exception) {
            debugLogger.info("Error while hashing file ${file.absolutePath}, ${e.message}")
            throw FileNotFoundException(e.message)
        }
    }

}