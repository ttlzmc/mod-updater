package org.ttlzmc.app

import com.sun.net.httpserver.Headers
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.ttlzmc.core.ModFinder

import org.ttlzmc.hwd.DwmAttribute
import org.ttlzmc.hwd.HwndLookupException
import org.ttlzmc.hwd.WindowHandle
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

        buttonSelectFolder.setOnMouseClicked {
            setStatusFieldText("Inspecting your mods...", Color.BLACK)
            debugLogger.info("Inspecting your mods...")
            val dirChooser = DirectoryChooser()
            dirChooser.title = "Select Mods Folder"
            val selectedDirectory = dirChooser.showDialog(stage)

            if (selectedDirectory != null && selectedDirectory.isDirectory) {
                textField.text = selectedDirectory.absolutePath // Устанавливаем текст в textField
            }
            ModFinder.onFileChosen(selectedDirectory)
        }

        val hBox = HBox().apply {
            spacing = 5.0
            alignment = Pos.CENTER
            children.addAll(textField, buttonSelectFolder)
        }

        root.alignment = Pos.CENTER
        root.style = "-fx-background-color: transparent"

        root.children.addAll(info, statusField, hBox)

        rootScene.heightProperty().addListener { _, _, height ->
            root.spacing = (height.toDouble() / 20)
        }

        rootScene.stylesheets.add("fluent-light.css")
        rootScene.fill = Color.TRANSPARENT
        stage.scene = rootScene
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