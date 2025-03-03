package org.ttlzmc.core

import com.google.gson.JsonParser
import javafx.scene.paint.Color
import org.ttlzmc.core.mod.Loader
import org.ttlzmc.core.mod.ModInfo
import org.ttlzmc.app.UpdaterWindow
import org.ttlzmc.utils.getString
import org.ttlzmc.utils.getStringOrElse
import java.io.File
import java.util.jar.JarFile
import java.util.logging.Logger

object ModFinder {

    var modsFolderFound: Boolean = false

    private val debugLogger = Logger.getLogger(SlugFinder::class.java.name)

    lateinit var foundMainLoader: Loader
    lateinit var selectedMinecraftVersion: MinecraftVersions.MinecraftVersion

    fun findMods(folder: File): List<ModInfo> {
        modsFolderFound = true
        val mods = findModInfo(folder)
        debugLogger.info("Total entries: ${mods.size}")
        if (mods.isEmpty()) {
            UpdaterWindow.status(
                "It seems like your mods folder is empty! Are you sure?",
                Color.LIGHTGRAY
            )
            throw RuntimeException("It seems like your mods folder is empty!")
        }
        val modsCount = modsCountByLoaders(mods)
        UpdaterWindow.status(modsCount, Color.LIGHTGRAY)
        return mods
    }

    private fun modsCountByLoaders(mods: List<ModInfo>): String {
        val fabricMods = mods.count { it.loader == Loader.FABRIC }
        val quiltMods = mods.count { it.loader == Loader.QUILT }
        val forgeMods = mods.count { it.loader == Loader.FORGE }
        return "Total: ${mods.size}" +
                if (fabricMods > 0) ", $fabricMods fabric" else "" +
                        if (quiltMods > 0) ", $quiltMods quilt, " else "" +
                                if (forgeMods > 0) ", $forgeMods forge, " else ""
    }

    private fun findModInfo(modsDir: File): List<ModInfo> {
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
                        UpdaterWindow.status(
                            "Selected folder is empty or loader type is not supported.",
                            Color.RED
                        )
                        throw RuntimeException("Selected folder is empty or loader type is not supported")
                    }
                }
            }
        }
        return mods
    }

    private fun extractFabricModInfo(jar: JarFile): ModInfo {
        val entry = jar.getInputStream(jar.getEntry("fabric.mod.json")).bufferedReader()
        val json = JsonParser.parseString(entry.readText()).asJsonObject
        debugLogger.info("New fabric.mod.json found in ${jar.name}")
        return ModInfo(
            modId = json.getString("id"),
            name = json.getStringOrElse("name") { "unnamed_${jar.name}" },
            description = json.getStringOrElse("description") { "No description provided" },
            version = json.getString("version"),
            loader = Loader.FABRIC
        )
    }

    private fun extractQuiltModInfo(jar: JarFile): ModInfo {
        val entry = jar.getInputStream(jar.getEntry("quilt.mod.json")).bufferedReader()
        val json = JsonParser.parseString(entry.readText()).asJsonObject
        debugLogger.info("New quilt.mod.json found in ${jar.name}")
        val modInfo = json.get("quilt_loader").asJsonObject
        val meta = modInfo.get("metadata").asJsonObject
        return ModInfo(
            modId = modInfo.get("id").asString,
            name = meta.getString("name"),
            description = meta.getString("description"),
            version = modInfo.get("version").asString,
            loader = Loader.QUILT
        )
    }

    private fun extractForgeModInfo(jar: JarFile): ModInfo {
        val entry = jar.getInputStream(jar.getEntry("META-INF/mods.toml")).bufferedReader().use { reader ->
            reader.readLines().joinToString("\n")
        }
        debugLogger.info("New mods.toml found in ${jar.name}")
        val lines = entry.split("\n")
        val modId = lines.find { it.startsWith("modId =") }?.substringAfter("= ")?.trim('"') ?: "unknown"
        val name = lines.find { it.startsWith("displayName =") }?.substringAfter("= ")?.trim('"') ?: "unknown"
        val description = lines.find { it.startsWith("description =") }?.substringAfter("= ")?.trim('"') ?: "unknown"
        val version = lines.find { it.startsWith("version =") }?.substringAfter("= ")?.trim('"') ?: "unknown"
        val loader = Loader.FORGE

        return ModInfo(modId, name, description, version, loader)
    }
}