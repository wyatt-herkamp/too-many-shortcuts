package dev.kingtux.tms.shortcuts


import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.KeyMapping
import net.minecraft.world.entity.player.Player
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import org.apache.logging.log4j.Level

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object TmsShortcuts : ClientModInitializer {
    const val MOD_ID = "tms_shortcuts"
     val SKIN_LAYER_CATEGORY: KeyMapping.Category = KeyMapping.Category.register(Identifier.withDefaultNamespace("${MOD_ID}.key.categories.skin_layers"))

    const val MOD_NAME = "Too Many Shortcuts Shortcuts"
    val LOGGER: Logger = LogManager.getLogger(TmsShortcuts.javaClass)
    lateinit var ESCAPE_KEYBINDING: KeyMapping;

    override fun onInitializeClient() {

    }

    fun setEscapeKeyBinding(keyBinding: KeyMapping) {
        ESCAPE_KEYBINDING = keyBinding
    }

    fun getEscapeKeyBinding(): KeyMapping {
        return ESCAPE_KEYBINDING
    }

    fun log(level: Level?, message: String) {
        LOGGER.log(level, "[$MOD_ID]$message")
    }

    fun sendToggleMessage(playerEntity: Player, value: Boolean, option: Component) {
        playerEntity.sendOverlayMessage(
            Component.translatable(
                "${MOD_ID}.toggled." + (if (value) "on" else "off"),
                option
            )
        )
    }
}