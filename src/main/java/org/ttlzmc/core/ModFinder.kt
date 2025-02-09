package org.ttlzmc.core

import javafx.scene.paint.Color
import org.json.JSONObject
import org.ttlzmc.app.UpdaterWindow
import org.ttlzmc.core.mod.Loader
import org.ttlzmc.core.mod.Mod
import org.ttlzmc.core.mod.ModInfo
import java.io.File
import java.util.jar.JarFile

object ModFinder {

    var modsFolderFound: Boolean = false

    fun onFileChosen(file: File): List<Mod> {
        val mods = inspect(file)
        if (mods.isEmpty()) {
            UpdaterWindow.setStatusFieldText(
                "It seems like your mods folder is empty! Are you sure?",
                Color.GRAY
            )
            throw RuntimeException("It seems like your mods folder is empty!")
        }
        val mapped = mods.map { Mod(it) }.toCollection(mutableListOf())
        val fabricSize = mapped.count { it.loader == Loader.FABRIC }
        val quiltSize = mapped.count { it.loader == Loader.QUILT }
        val forgeSize = mapped.count { it.loader == Loader.FORGE }
        val sizes = ", " +
                if (fabricSize > 0) "Fabric mods: $fabricSize" else "" +
                        if (quiltSize > 0) ", Qult mods: $quiltSize" else "" +
                                if (forgeSize > 0) ", Forge mods: $forgeSize" else ""

        UpdaterWindow.debugLogger.info("Found mods: ${mapped.size}")
        UpdaterWindow.setStatusFieldText("Found mods: ${mapped.size}" + sizes, Color.GRAY)
        modsFolderFound = true
        return mapped
    }

    fun inspect(modsDir: File): Collection<ModInfo> {
        val mods = mutableListOf<ModInfo>()

        val jars = modsDir.listFiles { file -> file.extension == "jar" } ?: return mods
        for (jar in jars) {
            JarFile(jar).use { mod ->
                when {
                    mod.getEntry("fabric.mod.json") != null -> {
                        val modInfo = extractFabricModInfo(mod)
                        mods.add(modInfo)
                    }
                    mod.getEntry("quilt.mod.json") != null -> {
                        val modInfo = extractQuiltModInfo(mod)
                        mods.add(modInfo)
                    }
                    mod.getEntry("META-INF/mods.toml") != null -> {
                        val modInfo = extractForgeModInfo(mod)
                        mods.add(modInfo)
                    }

                    else -> {
                        UpdaterWindow.setStatusFieldText(
                            "Mods folder is empty or loader type is not supported.",
                            Color.RED
                        )
                        throw RuntimeException("Mods folder is empty or loader type is not supported")
                    }
                }
            }
        }
        return mods
    }

    private fun extractFabricModInfo(jar: JarFile): ModInfo {
        val entry = jar.getInputStream(jar.getEntry("fabric.mod.json")).bufferedReader()
        val json = JSONObject(entry.readText())
        return ModInfo(
            modId = json.getString("id"),
            name = json.getString("name"),
            description = json.getString("description"),
            version = json.getString("version"),
            loader = Loader.FABRIC
        )
    }

    private fun extractQuiltModInfo(jar: JarFile): ModInfo {
        val entry = jar.getInputStream(jar.getEntry("quilt.mod.json")).bufferedReader()
        val json = JSONObject(entry.readText())
        return ModInfo(
            modId = json.getString("id"),
            name = json.getString("name"),
            description = json.getString("description"),
            version = json.getString("version"),
            loader = Loader.QUILT
        )
    }

    private fun extractForgeModInfo(jar: JarFile): ModInfo {
        val entry = jar.getInputStream(jar.getEntry("META-INF/mods.toml")).bufferedReader().use { reader ->
            reader.readLines().joinToString("\n")
        }

        val lines = entry.split("\n")
        val modId = lines.find { it.startsWith("modId =") }?.substringAfter("= ")?.trim('"') ?: "unknown"
        val name = lines.find { it.startsWith("displayName =") }?.substringAfter("= ")?.trim('"') ?: "unknown"
        val description = lines.find { it.startsWith("description =") }?.substringAfter("= ")?.trim('"') ?: "unknown"
        val version = lines.find { it.startsWith("version =") }?.substringAfter("= ")?.trim('"') ?: "unknown"
        val loader = Loader.FORGE

        return ModInfo(modId, name, description, version, loader)
    }
}