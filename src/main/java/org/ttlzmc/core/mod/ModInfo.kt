package org.ttlzmc.core.mod

import org.ttlzmc.core.SlugFinder

data class ModInfo(
    val modId: String,
    val name: String,
    val description: String,
    val version: String,
    val loader: Loader
) {
    val slug = SlugFinder.slugIfPresent(modId)
}
