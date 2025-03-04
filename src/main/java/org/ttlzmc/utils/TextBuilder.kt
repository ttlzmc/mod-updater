package org.ttlzmc.utils

import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text

object TextBuilder {

    fun newBuilder() = Builder()

    fun newBuilder(text: String) = Builder(text)

    private const val FONT_NAME = "Segoe UI Semibold"

    class Builder(text: String = "") {

        private var size = 10.0
        private val temp = Text(text)

        fun withColor(color: Color): Builder {
            temp.fill = color
            return this
        }

        fun withText(text: String): Builder {
            temp.text = text
            return this
        }

        fun withFont(name: String): Builder {
            temp.font = Font.font(name, this.size)
            return this
        }

        fun withFontSize(size: Int): Builder {
            this.size = size.toDouble()
            temp.font = Font.font(FONT_NAME, this.size)
            return this
        }

        fun withFontSize(float: Float): Builder {
            this.size = float.toDouble()
            temp.font = Font.font(FONT_NAME, this.size)
            return this
        }

        fun withFontSize(size: Double): Builder {
            this.size = size
            temp.font = Font.font(FONT_NAME, this.size)
            return this
        }

        fun bold(): Builder {
            temp.font = Font.font(FONT_NAME, FontWeight.BOLD, this.size)
            return this
        }

        fun build() = temp
    }
}