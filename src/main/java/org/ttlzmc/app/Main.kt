package org.ttlzmc.app

import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.ttlzmc.core.Downloader.onFileChosen

import org.ttlzmc.hwd.DwmAttribute
import org.ttlzmc.hwd.HwndLookupException
import org.ttlzmc.hwd.WindowHandle

fun main() {
    Application.launch(UpdaterWindow::class.java)
}

class UpdaterWindow : Application() {

    private val root = VBox()

    override fun start(stage: Stage) {

        val info = Text("Select your version mods folder.")
        val statusField = Text("<status>")

        val button = Button("Select folder").apply {
            prefWidth = 200.0
            prefHeight = 50.0
        }

        button.setOnMouseClicked {
            val dirChooser = DirectoryChooser()
            dirChooser.title = "Select Mods Folder"
            val selectedDirectory = dirChooser.showDialog(stage)

            if (selectedDirectory != null && selectedDirectory.isDirectory) {
                onFileChosen(selectedDirectory)
            }
        }

        root.alignment = Pos.CENTER
        root.style = "-fx-background-color: transparent"

        root.children.addAll(info, statusField, button)

        val scene = Scene(root, 400.0, 300.0)

        scene.heightProperty().addListener { _, _, height ->
            root.spacing = (height.toDouble() / 20)
        }

        scene.stylesheets.add("fluent-light.css")
        scene.fill = Color.TRANSPARENT
        stage.scene = scene
        stage.initStyle(StageStyle.UNIFIED)
        stage.title = "welcome-stage"

        this.setMica(stage, true)

        stage.show()
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