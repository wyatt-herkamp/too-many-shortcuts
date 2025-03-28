package dev.kingtux.tms

import dev.kingtux.tms.api.TMSKeyBinding


import dev.kingtux.tms.api.modifiers.BindingModifiers
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object TooManyShortcuts : ClientModInitializer {
    const val MOD_ID = "too_many_shortcuts"

    const val MOD_NAME = "Too Many Shortcuts"
    const val SKIN_LAYER_CATEGORY: String = "$MOD_ID.key.categories.skin_layers"

    val LOGGER: Logger = LogManager.getLogger(TooManyShortcuts.javaClass)

    val ESCAPE_KEYBINDING: KeyBinding = KeyBindingHelper.registerKeyBinding(
        TMSKeyBinding(
            Identifier.of(MOD_ID, "alternative_escape"),
            InputUtil.Type.KEYSYM,
            -1,
            "key.categories.ui",
            BindingModifiers()
        )
    )


    override fun onInitializeClient() {

    }


    fun log(level: Level?, message: String) {
        LOGGER.log(level, "[$MOD_ID]$message")
    }

    fun getEscapeKeyBinding(): KeyBinding {
        return ESCAPE_KEYBINDING
    }

    fun makeKeyID(keyName: String): String {
        return "key.$MOD_NAME.$keyName"
    }

}
