package org.ttlzmc.core

import com.google.gson.JsonParser
import org.ttlzmc.utils.getString
import java.net.HttpURLConnection
import java.net.URI
import java.util.logging.Logger

/**
 * A class that loads slus list directly from github,
 *
 * so that users don't have to update the program every time a change is made.
 */
object SlugFinder {

    private val slugs = LinkedHashMap<String, String>()

    fun load() {
        val url = URI("https://raw.githubusercontent.com/ttlzmc/mod-updater/refs/heads/main/src/main/resources/slugs.json")
            .toURL()
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.connect()
        val json = JsonParser.parseString(connection.inputStream.bufferedReader().readText()).asJsonObject
        for (key in json.keySet()) {
            slugs[key] = json.getString(key)
        }
        Logger.getAnonymousLogger().info(slugs.toString())
    }

    fun slugIfPresent(key: String): String {
        return slugs[key] ?: key
    }

}