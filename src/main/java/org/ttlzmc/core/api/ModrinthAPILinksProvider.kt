package org.ttlzmc.core.api

import org.ttlzmc.core.mod.ModInfo
import java.net.URI
import java.net.URL

object ModrinthAPILinksProvider {

    fun getProject(modName: String): URL {
        return URI.create("https://api.modrinth.com/v2/project/${completeModName(modName)}").toURL()
    }

    fun getProjects(vararg modName: String): List<URL> {
        return modName.map { getProject(it) }
    }

    fun getProjectIcon(mod: ModInfo): URL {
        return URI.create(ModrinthAPIProvider.getProjectRaw(mod).getString("icon_url")).toURL()
    }

    fun completeModName(modName: String): String = modName.replace(" ", "-").lowercase()
}