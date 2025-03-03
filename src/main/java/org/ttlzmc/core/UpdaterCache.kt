package org.ttlzmc.core

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.io.File
import java.io.InputStream

object UpdaterCache {

    fun cacheIcon(name: String, stream: InputStream) {
        val temp = File(cacheFolder().path + "/icons", name).apply { createNewFile() }
        stream.use { input -> temp.outputStream().use { output -> input.copyTo(output) } }
    }

    fun loadIcon(name: String): ImageView {
        val file = File(cacheFolder().path + "/icons/$name")
        if (!file.exists() || !file.isDirectory) { throw RuntimeException("Failed to load icon $name") }
        if (file.extension != "png") {
            return IconFormatConverter.convert(cacheFolder().path + "/icons/$name", cacheFolder().path + "/converted/$name")
        }
        return ImageView(Image(file.inputStream()))
    }

    fun cacheFolder(): File {
        val homeDir = System.getProperty("user.home")
        val updaterCacheDir = File(homeDir, "UpdaterCache")
        if (!updaterCacheDir.exists()) updaterCacheDir.mkdirs()
        return updaterCacheDir
    }
}