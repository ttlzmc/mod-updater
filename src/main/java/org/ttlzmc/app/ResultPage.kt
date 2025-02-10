package org.ttlzmc.app

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ContextMenu
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import org.ttlzmc.core.mod.Loader
import org.ttlzmc.core.mod.ModInfo
import org.ttlzmc.minecraft.MinecraftVersions
import org.ttlzmc.utils.TextBuilder

object ResultPage {

    private lateinit var loader: Loader
    private lateinit var foundMods: List<ModInfo>
    private lateinit var minecraftVersion: MinecraftVersions.MinecraftVersion

    private val root = BorderPane().apply {
        prefHeight = 600.0
        prefWidth = 400.0
        style = "-fx-background-color: transparent"
    }

    private lateinit var holder: VBox
    private lateinit var listContainer: VBox

    private lateinit var title: Text
    private lateinit var loaderField: Text
    private lateinit var modsField: Text

    private val modsList: ScrollPane = ScrollPane()

    fun init(loader: Loader, foundMods: List<ModInfo>, version: MinecraftVersions.MinecraftVersion): BorderPane {
        this.loader = loader
        this.foundMods = foundMods
        this.minecraftVersion = version

        this.initComponents()

        return root
    }

    private fun initComponents() {

        title = TextBuilder.newBuilder("Now, let's see what we got:")
            .withFontSize(25)
            .build()

        loaderField = TextBuilder.newBuilder("Loader: ${loader.key}, Version: ${minecraftVersion.value}")
            .withFontSize(16)
            .withColor(Color.GRAY)
            .build()

        modsField = TextBuilder.newBuilder("Mods found: ${foundMods.size}")
            .withFontSize(16)
            .build()

        modsList.apply {
            maxWidth = 400.0
            maxHeight = 800.0
            prefHeight = 600.0
            layoutX = root.width / 2
        }

        holder = VBox().apply {
            spacing = 10.0
            alignment = Pos.CENTER
            children.addAll(title, loaderField, modsField)
        }

        listContainer = VBox().apply {
            spacing = 10.0
            alignment = Pos.CENTER
            children.add(modsList)
        }

        root.apply {
            center = holder
            bottom = listContainer
            padding = Insets(15.0, 15.0, 15.0, 15.0)
            heightProperty().addListener { _, _, newValue ->
                modsList.maxHeight = (newValue as Double / 10) * 7.2
            }
        }
    }
}