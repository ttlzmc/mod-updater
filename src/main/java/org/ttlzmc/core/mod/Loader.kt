package org.ttlzmc.core.mod

enum class Loader(val key: String) {
    FABRIC("fabric"),
    QUILT("quilt"),
    FORGE("forge"),
    NEOFORGE("neoforge")
    //TODO: Add NeoForge to ModFinder
    ;
}