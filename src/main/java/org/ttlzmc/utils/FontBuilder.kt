package org.ttlzmc.utils

import javafx.scene.text.Font

object FontBuilder {

    private const val FONT_NAME = "Segoe UI Semibold"

    fun sizeOf(double: Double): Font {
        return Font.font(FONT_NAME, double)
    }

    fun sizeOf(int: Int): Font {
        return Font.font(FONT_NAME, int.toDouble())
    }

}