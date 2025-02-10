package org.ttlzmc.updater

import javafx.scene.control.Label
import javafx.scene.layout.VBox
import org.ttlzmc.core.mod.Loader
import org.ttlzmc.minecraft.MinecraftVersion
import org.ttlzmc.utils.FontBuilder

object ResultPage {

    private lateinit var loader: Loader
    private lateinit var foundMods: List<Mod>
    private lateinit var minecraftVersion: MinecraftVersion

    private val root = VBox()

    private lateinit var title: Label
    private lateinit var subtitle: Label

    fun init(): VBox {
        this.initComponents()
        return root
    }

    private fun initComponents() {
        title = Label("Now, let's see what we got:").apply {
            font = FontBuilder.sizeOf(20)
        }

        subtitle = Label("Loader: ${loader.key}").apply {
            font = FontBuilder.sizeOf(15)
        }

    }
}