package org.ttlzmc.core.api

import javafx.scene.image.Image
import org.json.JSONObject
import org.ttlzmc.core.mod.Mod
import java.io.File
import java.net.HttpURLConnection

object ModrinthAPIProvider {

    fun getProject(mod: Mod, to: File): File {
        val json = getProjectRaw(mod)
        return File("")
    }

    fun getProjectRaw(mod: Mod): JSONObject {
        val url = ModrinthAPILinksProvider.getProject(mod.name)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 6000
        connection.readTimeout = 6000
        connection.connect()
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            return JSONObject(connection.inputStream.bufferedReader().readText())
        }
        throw RuntimeException("Error getting project ${mod.name}")
    }

    fun getProjectIcon(mod: Mod): Image {
        val cacheFolder = File(System.getProperty("user.home"), "caches")
        if (!cacheFolder.exists()) cacheFolder.mkdirs()
        val downloadUrl = ModrinthAPILinksProvider.getProjectIcon(mod)
        val connection = downloadUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 6000
        connection.readTimeout = 6000
        connection.connect()
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val temp = File.createTempFile(mod.modId, ".webp", cacheFolder)
            connection.inputStream.use { input -> temp.outputStream().use { output -> input.copyTo(output) } }
            return Image(temp.path)
        }
        throw RuntimeException("Error getting project ${mod.name} icon")
    }

}