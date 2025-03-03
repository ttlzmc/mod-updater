package org.ttlzmc.core

import com.groupdocs.conversion.Converter
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.ttlzmc.core.UpdaterCache.cacheFolder
import java.io.File

object IconFormatConverter {

    fun convert(path: String, to: String): ImageView {
        loadAndConvert(path, to)
        return loadReadyIcon(path)
    }

    private fun loadReadyIcon(path: String): ImageView {
        val cacheFolder = cacheFolder()
        val source = File(cacheFolder.path + "/" + path).inputStream()
        return ImageView(Image(source))
    }

    private fun loadAndConvert(path: String, to: String) {
        val cacheFolder = cacheFolder()
        val source = cacheFolder.path + "/" + path
        val ready = cacheFolder.path + "/" + to
        Converter().load(source).convertTo(ready).convert()
    }
}