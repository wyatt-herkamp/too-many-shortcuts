package dev.kingtux.tms.shortcuts


import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.option.KeyBinding
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import org.apache.logging.log4j.Level

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object TmsShortcuts: ClientModInitializer {
    const val MOD_ID = "tms_shortcuts"
    const val SKIN_LAYER_CATEGORY: String = "${MOD_ID}.key.categories.skin_layers"

    const val MOD_NAME = "Too Many Shortcuts Shortcuts"
    val LOGGER: Logger = LogManager.getLogger(TmsShortcuts.javaClass)
    lateinit var ESCAPE_KEYBINDING: KeyBinding;

    override fun onInitializeClient() {

    }
    fun setEscapeKeyBinding(keyBinding: KeyBinding) {
        ESCAPE_KEYBINDING = keyBinding
    }
    fun getEscapeKeyBinding(): KeyBinding {
        return ESCAPE_KEYBINDING
    }
    fun log(level: Level?, message: String) {
        LOGGER.log(level, "[$MOD_ID]$message")
    }
    fun sendToggleMessage(playerEntity: PlayerEntity, value: Boolean, option: Text?) {
        playerEntity.sendMessage(
            Text.translatable(
                "${MOD_ID}.toggled." + (if (value) "on" else "off"),
                option
            ), true
        )
    }
}