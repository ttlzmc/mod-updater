package org.ttlzmc.app

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import org.ttlzmc.core.mod.Mod
import org.ttlzmc.minecraft.MinecraftVersion

object ProcessWindow {

    fun init(mods: List<Mod>, version: MinecraftVersion): BorderPane {
        val pane = BorderPane()
        pane.prefWidth = 300.0
        pane.prefHeight = 500.0
        pane.style = "-fx-background-color: transparent"

        val vbox = VBox()
        vbox.spacing = 10.0
        vbox.alignment = Pos.CENTER
        vbox.padding = Insets(30.0, 20.0, 20.0, 20.0)

        val label = Text("Now, let's see what we got:")
        label.font = FontBuilder.sizeOfi(20)

        val scrollPane = ScrollPane()
        scrollPane.maxWidth = 500.0
        scrollPane.prefHeight = 700.0

        vbox.children.add(label)
        vbox.children.add(scrollPane)

        pane.center = vbox

        pane.heightProperty().addListener { _, _, newValue ->
            scrollPane.maxHeight = (newValue.toDouble() / 8) * 7
        }

        return pane
    }

}