package org.ttlzmc.core.mod

data class ModInfo(
    val modId: String,
    val name: String,
    val description: String,
    val version: String,
    val loader: Loader
)
