package org.ttlzmc.minecraft

enum class MinecraftVersion(val string: String) {
    _1_21_4("1.21.4"),
    _1_21("1.21"),
    _1_20_6("1.20.6"),
    _1_20_5("1.20.5"),
    _1_20_4("1.20.4"),
    _1_20_3("1.20.3"),
    _1_20_2("1.20.2"),
    _1_20_1("1.20")
    ;

    companion object {
        fun isSupported(ver: String): Boolean {
            return MinecraftVersion.entries.any { it.string == ver }
        }
    }
}