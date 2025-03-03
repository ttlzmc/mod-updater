package org.ttlzmc.core.api

import com.google.gson.JsonObject
import org.ttlzmc.core.SlugFinder
import org.ttlzmc.core.mod.ModInfo
import org.ttlzmc.utils.getString
import java.net.URI
import java.net.URL

/**
 * An object that provides ready-to-use links to specific Modrinth API methods.
 * @see ModrinthAPIProvider
 */
object ModrinthAPILinksProvider {

    fun getProject(mod: ModInfo): URL {
        return URI.create("https://api.modrinth.com/v2/project/${slug(mod.modId)}").toURL()
    }

    fun getProjectIcon(apiResponse: JsonObject): URL {
        return URI.create(apiResponse.getString("icon_url")).toURL()
    }

    fun listProjectVersions(mod: ModInfo): URL {
        return URI.create("https://api.modrinth.com/v2/project/${slug(mod.modId)}/version").toURL()
    }

    fun getProjectVersion(base62version: String): URL {
        return URI.create("https://api.modrinth.com/v2/version/$base62version").toURL()
    }

    private fun slug(modId: String): String {
        return SlugFinder.slugIfPresent(modId.replace(" ", "-")
            .replace("_", "-").lowercase())
    }
}