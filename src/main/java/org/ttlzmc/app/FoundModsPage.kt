package org.ttlzmc.app

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import org.ttlzmc.core.ModFinder
import org.ttlzmc.core.mod.ModrinthMod
import org.ttlzmc.core.mod.Loader
import org.ttlzmc.core.mod.ModInfo
import org.ttlzmc.core.MinecraftVersions
import org.ttlzmc.core.api.LabrinthAPIProvider
import org.ttlzmc.utils.TextBuilder
import java.util.concurrent.CompletableFuture

object FoundModsPage {

    private lateinit var loader: Loader
    private lateinit var foundMods: List<ModInfo>
    private lateinit var minecraftVersion: MinecraftVersions.MinecraftVersion

    private val root = BorderPane().apply {
        prefHeight = 600.0
        prefWidth = 400.0
    }

    private lateinit var textInfoHolder: VBox
    private lateinit var modViewsHolder: VBox

    private lateinit var title: Text
    private lateinit var statusField: Text
    private lateinit var loaderField: Text
    private lateinit var modsField: Text

    private lateinit var updateButton: Button

    private val modViewsList: ScrollPane = ScrollPane()

    fun init(loader: Loader, foundMods: List<ModInfo>, version: MinecraftVersions.MinecraftVersion): BorderPane {
        ModFinder.foundMainLoader = loader
        ModFinder.selectedMinecraftVersion = version

        this.loader = loader
        this.foundMods = foundMods
        this.minecraftVersion = version

        this.initComponents()
        this.generateModsList()

        return root
    }

    private fun generateModsList() = CompletableFuture.runAsync {
        val container = VBox()
        container.spacing = 10.0
        container.alignment = Pos.CENTER
        container.padding = Insets(8.0)
        this.modViewsList.content = container
        foundMods.map { info ->
            LabrinthAPIProvider.getProject(info).run {
                Platform.runLater { container.children.add(createModView(this)) }
            }
        }.apply {
            updateButton.isDisable = false
        }
    }

    private fun initComponents() {
        title = TextBuilder.newBuilder("Now, let's see what we got:")
            .withFontSize(25)
            .build()

        statusField = TextBuilder.newBuilder("Fetching your mods, please wait!")
            .withFontSize(16)
            .build()

        loaderField = TextBuilder.newBuilder("Loader: ${loader.key}, Version: ${minecraftVersion.value}")
            .withFontSize(16)
            .withColor(Color.GRAY)
            .build()

        modsField = TextBuilder.newBuilder("Mods found: ${foundMods.size}")
            .withFontSize(16)
            .build()

        updateButton = Button("Update").apply {
            isDisable = true
            alignment = Pos.CENTER
            padding = Insets(10.0)
        }

        modViewsList.apply {
            maxWidth = 400.0
            maxHeight = 800.0
            prefHeight = 600.0
            layoutX = root.width / 2
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        }

        textInfoHolder = VBox().apply {
            padding = Insets(10.0, 10.0, 10.0, 10.0)
            spacing = 10.0
            alignment = Pos.CENTER
            children.addAll(title, loaderField, modsField)
        }

        modViewsHolder = VBox().apply {
            spacing = 10.0
            padding = Insets(10.0)
            alignment = Pos.CENTER
            children.add(modViewsList)
        }

        root.apply {
            top = textInfoHolder
            center = modViewsHolder.apply {
                layoutY += 15
            }
            bottom = VBox(updateButton).apply {
                alignment = Pos.CENTER
            }
            padding = Insets(15.0, 15.0, 15.0, 15.0)
            heightProperty().addListener { _, _, newValue ->
                modViewsList.maxHeight = (newValue as Double / 10) * 7.2
            }
        }
    }

    private fun createModView(mod: ModrinthMod): HBox {
        val wrapper = HBox(5.0)
        val name = Text(mod.name).apply {
            if (text.length >= 32) {
                text = text.substring(0..32) + ".."
            }
            font = Font.font(font.family, FontWeight.BOLD, 14.0)
        }
        val desc = Text(mod.description).apply {
            font = Font.font(12.0)
            wrappingWidth = 270.0
        }
        val icon = LabrinthAPIProvider.getProjectIcon(mod)?.apply {
            fitWidth = 64.0
            fitHeight = 64.0
            clip = Rectangle(64.0, 64.0).apply {
                arcWidth = 15.0
                arcHeight = 15.0
            }
        }
        val info = VBox(5.0).apply {
            children.addAll(name, desc)
        }
        wrapper.children.addAll(icon, info)
        return wrapper
    }
}