package org.ttlzmc.core.mod

import java.io.File

/**
 * Found ModInfo with fetched info from `fabric.mod.json` or
 *
 * other info files used in [org.ttlzmc.core.ModFinder]
 */
data class ModInfo(
    val name: String,
    val description: String,
    val minecraftVersion: String,
    val loader: Loader,
    val jarFile: File
)
