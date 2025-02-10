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
import org.ttlzmc.core.mod.Mod

import org.ttlzmc.hwd.DwmAttribute
import org.ttlzmc.hwd.HwndLookupException
import org.ttlzmc.hwd.WindowHandle
import org.ttlzmc.minecraft.MinecraftVersion
import org.ttlzmc.utils.FontBuilder
import java.io.File
import java.util.logging.Logger

fun main() {
    Application.launch(UpdaterWindow::class.java)
}

class UpdaterWindow : Application() {

    companion object {
        private val statusField = Text().apply { text = "" }

        val debugLogger: Logger = Logger.getLogger("Debug")

        fun setStatusFieldText(str: String, fill: Color) {
            debugLogger.info("Status Field changed: $str")
            statusField.textProperty().set(str)
            statusField.fillProperty().set(fill)
        }
    }

    private val root = VBox()
    private val rootScene = Scene(root, 500.0, 300.0)

    override fun start(stage: Stage) {

        lateinit var mods: List<Mod>
        lateinit var minecraftVersion: MinecraftVersion

        val info = Text("Select your version mods folder.").apply {
            font = FontBuilder.sizeOf(25.0)
        }

        val textField = TextField("...").apply {
            isEditable = false
            prefWidth = 300.0
        }

        val buttonSelectFolder = Button("...").apply {
            prefWidth = 20.0
            prefHeight = 20.0
        }

        val buttonProceed = Button("Continue").apply {
            prefWidth = 100.0
            prefHeight = 5.0

            setOnMouseClicked {
                if (ModFinder.modsFolderFound) {
                    stage.scene = Scene(ProcessWindow.init(mods, minecraftVersion)).apply {
                        stylesheets.add("fluent-light.css")
                        style = "-fx-background-color: transparent"
                        fill = Color.TRANSPARENT
                    }
                    stage.sizeToScene()
                } else {
                    setStatusFieldText("Mods folder not found!", Color.RED)
                }
            }
        }

        buttonSelectFolder.setOnMouseClicked {
            setStatusFieldText("Inspecting your mods...", Color.BLACK)
            debugLogger.info("Inspecting your mods...")
            val dirChooser = DirectoryChooser()
            dirChooser.initialDirectory = File(System.getProperty("user.home"), "AppData")
            dirChooser.title = "Select Mods Folder"
            val selectedDirectory = dirChooser.showDialog(stage)

            if (selectedDirectory != null && selectedDirectory.isDirectory) {
                textField.text = selectedDirectory.absolutePath
            }
            mods = ModFinder.onFileChosen(selectedDirectory)
        }

        val folderSelectionLevel = HBox().apply {
            alignment = Pos.CENTER
            children.addAll(textField, buttonSelectFolder)
        }

        val selectVersionText = Text("Version").apply {
            font = FontBuilder.sizeOf(15.0)
        }

        val items = arrayListOf<MenuItem>()
        for (version in MinecraftVersion.entries) {
            items.add(MenuItem(version.string).apply {
                setOnAction {
                    selectVersionText.text = version.string
                    minecraftVersion = version
                }
            })
        }

        val versionsContextMenu = ContextMenu().apply {
            items.addAll(items)
        }

        val selectedVersionTextField = TextField().apply {
            isEditable = false
            prefHeight = 5.0
            prefWidth = 120.0
        }

        val openContextMenuButton = Button("Select").apply {
            prefWidth = 20.0
            prefHeight = 5.0
            setOnMouseClicked {
                openContextMenu(this, versionsContextMenu)
            }
        }

        val versionSelectionLevel = HBox().apply {
            spacing = 5.0
            alignment = Pos.CENTER
            children.addAll(selectVersionText, selectedVersionTextField, openContextMenuButton)
        }

        root.alignment = Pos.CENTER
        root.spacing = 15.0
        root.style = "-fx-background-color: transparent"

        root.children.addAll(info, statusField, folderSelectionLevel, versionSelectionLevel ,buttonProceed)

        rootScene.heightProperty().addListener { _, _, height ->
            root.spacing = (height.toDouble() / 20)
        }

        rootScene.stylesheets.add("fluent-light.css")
        rootScene.fill = Color.TRANSPARENT
        stage.scene = rootScene
        stage.initStyle(StageStyle.UNIFIED)
        stage.title = "Modpack Updater"

        this.setMica(stage, true)

        stage.show()
    }

    private fun openContextMenu(openButton: Button, menu: ContextMenu) {
        val scene = openButton.scene
        menu.show(
            scene.window,
            scene.window.x + openButton.layoutX,
            scene.window.y + 25 + openButton.layoutY,
        )
    }

    private fun setMica(stage: Stage, set: Boolean) {
        Platform.runLater {
            try {
                val handle = WindowHandle.tryFind(stage)
                handle.dwmSetBooleanValue(DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, set)
                if (!handle.dwmSetIntValue(DwmAttribute.DWMWA_SYSTEMBACKDROP_TYPE, DwmAttribute.DWMSBT_MAINWINDOW.value)
                ) {
                    handle.dwmSetBooleanValue(DwmAttribute.DWMWA_MICA_EFFECT, set)
                }
            } catch (ignored: HwndLookupException) { }
        }
    }

}