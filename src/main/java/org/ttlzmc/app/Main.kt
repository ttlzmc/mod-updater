package org.ttlzmc.app

import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.ttlzmc.core.ModFinder
import org.ttlzmc.core.mod.Loader
import org.ttlzmc.core.mod.ModInfo
import org.ttlzmc.hwd.DwmAttribute
import org.ttlzmc.hwd.HwndLookupException
import org.ttlzmc.hwd.WindowHandle
import org.ttlzmc.core.MinecraftVersions
import org.ttlzmc.utils.FontBuilder
import org.ttlzmc.utils.TextBuilder
import java.io.File

fun main(args: Array<String>) {
    MinecraftVersions.load()
    Application.launch(UpdaterWindow::class.java, *args)
}

class UpdaterWindow : Application() {

    private val root = VBox().apply {
        alignment = Pos.CENTER
        spacing = 15.0
        style = "-fx-background-color: transparent;"
    }

    private val folderSelectionLevel = HBox().apply {
        alignment = Pos.CENTER
    }

    private val versionSelectionLevel = HBox().apply {
        alignment = Pos.CENTER
    }

    private val rootScene = Scene(root, 500.0, 300.0)

    private lateinit var primaryStage: Stage
    private lateinit var selectedMinecraftVersion: MinecraftVersions.MinecraftVersion
    private lateinit var foundMods: List<ModInfo>

    private lateinit var info: Text
    private lateinit var infoSub: Text

    private val path = TextField("...")
    private val selectFolder = Button("...")
    private val proceed = Button("Continue")

    private lateinit var versionInfo: Text
    private val selectedVersion = TextField("...")
    private val selectVersionButton = Button("Select")
    private val contextMenu = ContextMenu()

    override fun start(stage: Stage) {
        this.primaryStage = stage

        this.initComponents()
        this.enableMica()

        this.primaryStage.show()
    }

    private fun initComponents() {
        info = TextBuilder.newBuilder()
            .withText("Let's start from here")
            .withFontSize(20)
            .build()

        infoSub = TextBuilder.newBuilder()
            .withText("Please specify minecraft version and path to mods folder.")
            .withFontSize(15)
            .build()

        path.apply {
            isEditable = false
            prefWidth = 300.0
        }
        selectFolder.apply {
            prefWidth = 20.0
            prefHeight = 20.0

            setOnMouseClicked {
                val dirChooser = DirectoryChooser()
                dirChooser.initialDirectory = File(System.getProperty("user.home"), "AppData")
                dirChooser.title = "Select mods folder."
                val selectedDirectory = dirChooser.showDialog(primaryStage)
                if (selectedDirectory != null && selectedDirectory.isDirectory) {
                    path.text = selectedDirectory.absolutePath
                    this@UpdaterWindow.foundMods = ModFinder.findMods(selectedDirectory)
                }
            }
        }
        proceed.apply {
            prefWidth = 100.0
            prefHeight = 5.0

            setOnMouseClicked {
                if (ModFinder.modsFolderFound) {
                    primaryStage.scene = Scene(ResultPage.init(
                        Loader.FABRIC, foundMods, selectedMinecraftVersion
                    )).apply {
                        stylesheets.add("fluent-light.css")
                        fill = Color.TRANSPARENT
                    }
                    primaryStage.sizeToScene()
                } else {
                    status("Mods folder not found!", Color.RED)
                }
            }
        }

        versionInfo = TextBuilder.newBuilder()
            .withText("Version:    ")
            .withFontSize(15)
            .build()

        selectedVersion.apply {
            isEditable = false
            prefWidth = 120.0
            prefHeight = 5.0
        }
        selectVersionButton.apply {
            prefWidth = 20.0
            prefHeight = 5.0
            setOnMouseClicked {
                openContextMenu()
            }
        }

        val items = arrayListOf<MenuItem>()
        for (version in MinecraftVersions.entries()) {
            items.add(MenuItem(version.value).apply {
                setOnAction {
                    selectedVersion.text = version.value
                }
            })
        }
        selectedMinecraftVersion = MinecraftVersions.entries().first()
        selectedVersion.text = selectedMinecraftVersion.value

        contextMenu.apply {
            this.items.addAll(items)
        }

        rootScene.apply {
            stylesheets.add("fluent-light.css")
            fill = Color.TRANSPARENT

            heightProperty().addListener { _, _, newValue ->
                this@UpdaterWindow.root.spacing = newValue.toDouble() / 20
            }
        }

        primaryStage.scene = rootScene
        primaryStage.initStyle(StageStyle.UNIFIED)
        primaryStage.title = "Modpack Updater"

        folderSelectionLevel.children.addAll(path, selectFolder)
        versionSelectionLevel.children.addAll(versionInfo, selectedVersion, selectVersionButton)

        root.children.addAll(info, infoSub, statusField, folderSelectionLevel, versionSelectionLevel, proceed)
    }

    private fun openContextMenu() {
        contextMenu.show(
            rootScene.window,
            rootScene.window.x + selectedVersion.layoutX,
            rootScene.height + 25 + selectedVersion.layoutY
        )
    }

    private fun enableMica() {
        Platform.runLater {
            try {
                val handle = WindowHandle.tryFind(primaryStage)
                handle.dwmSetBooleanValue(DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, true)
                if (!handle.dwmSetIntValue(DwmAttribute.DWMWA_SYSTEMBACKDROP_TYPE,
                    DwmAttribute.DWMSBT_MAINWINDOW.value
                )) {
                    handle.dwmSetBooleanValue(DwmAttribute.DWMWA_MICA_EFFECT, true)
                }
            } catch (ignored: HwndLookupException) {}
        }
    }

    companion object {
        private val statusField = Text("").apply {
            font = FontBuilder.sizeOf(12)
        }

        fun status(string: String, fill: Color) {
            statusField.textProperty().set(string)
            statusField.fillProperty().set(fill)
        }
    }
}