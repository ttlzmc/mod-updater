package org.ttlzmc.utils

import javafx.scene.paint.Color
import javafx.scene.text.Text

object TextBuilder {

    fun newBuilder() = Builder()

    class Builder(text: String = "") {

        private val temp = Text(text)

        fun withColor(color: Color): Builder {
            temp.fill = color
            return this
        }

        fun withText(text: String): Builder {
            temp.text = text
            return this
        }

        fun withFontSize(size: Int): Builder {
            temp.font = FontBuilder.sizeOf(size)
            return this
        }

        fun withFontSize(float: Float): Builder {
            temp.font = FontBuilder.sizeOf(float.toDouble())
            return this
        }

        fun build() = temp
    }
}