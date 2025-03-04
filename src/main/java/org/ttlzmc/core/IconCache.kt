package org.ttlzmc.core

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO

object IconCache {

    fun cacheIcon(name: String, stream: InputStream) {
        val icons = File(cacheFolder().path + "/icons").apply { mkdirs() }
        val temp = File("${icons.path}/$name").apply { createNewFile() }
        stream.use { input -> temp.outputStream().use { output -> input.copyTo(output) } }
    }

    fun loadIcon(name: String): ImageView? {
        val file = File(cacheFolder().path + "/icons/$name")
        if (!file.exists()) return null
        if (file.extension == "webp") {
            return convertImage(cacheFolder().path + "/icons/$name")
        }
        return ImageView(Image(file.inputStream()))
    }

    fun cacheFolder(): File {
        val homeDir = System.getProperty("user.home")
        val updaterCacheDir = File(homeDir, "UpdaterCache")
        if (!updaterCacheDir.exists()) updaterCacheDir.mkdirs()
        return updaterCacheDir
    }

    private fun convertImage(path: String): ImageView {
        val image = ImageIO.read(File(path))
        var wr: WritableImage? = null
        if (image != null) {
            wr = WritableImage(image.width, image.height)
            val pw = wr.pixelWriter
            for (x in 0..<image.width) {
                for (y in 0..<image.height) {
                    pw.setArgb(x, y, image.getRGB(x, y))
                }
            }
        }
        return ImageView(wr)
    }
}