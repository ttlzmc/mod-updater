package org.ttlzmc.core.mod

import org.ttlzmc.core.api.ModrinthMod

//TODO
class FoundMods(val loader: Loader, val mods: List<ModInfo>) {
    lateinit var fetchedProjects: List<ModrinthMod>
}