package dev.kingtux.tms

import dev.kingtux.tms.api.TMSKeyBinding
import dev.kingtux.tms.keybinding.SkinLayerKeyBinding
import dev.kingtux.tms.keybinding.ToggleAutoJumpKeyBinding

import dev.kingtux.tms.api.modifiers.BindingModifiers
import dev.kingtux.tms.gui.TMSKeyBindingEntry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.render.entity.PlayerModelPart
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

object TooManyShortcuts : ClientModInitializer {
    const val MOD_ID = "too_many_shortcuts"

    const val MOD_NAME = "Too Many Shortcuts"
    const val SKIN_LAYER_CATEGORY: String = "$MOD_ID.key.categories.skin_layers"

    val LOGGER: Logger = LogManager.getLogger(MOD_NAME)
    const val TRIGGER_KEYBINDING_ON_SCROLL: Boolean = true
    val currentModifiers: BindingModifiers = BindingModifiers()

    val ESCAPE_KEYBINDING: KeyBinding = KeyBindingHelper.registerKeyBinding(
        TMSKeyBinding(
            Identifier(MOD_ID, "alternative_escape"),
            InputUtil.Type.KEYSYM,
            -1,
            "key.categories.ui",
            BindingModifiers()
        )
    )


    override fun onInitializeClient() {

        KeyBindingHelper.registerKeyBinding(
            ToggleAutoJumpKeyBinding(
                Identifier(MOD_ID, "toggle_auto_jump"),
                InputUtil.Type.KEYSYM,
                66,
                "key.categories.movement",
                BindingModifiers()
            )
        )

        Arrays.stream(PlayerModelPart.entries.toTypedArray())
            .map { playerModelPart: PlayerModelPart ->
                SkinLayerKeyBinding(
                    Identifier(MOD_ID, "toggle_" + playerModelPart.getName().lowercase()),
                    InputUtil.Type.KEYSYM,
                    -1,
                    SKIN_LAYER_CATEGORY,
                    playerModelPart
                )
            }
            .forEach { keyBinding: SkinLayerKeyBinding? ->
                KeyBindingHelper.registerKeyBinding(
                    keyBinding
                )
            }
    }

    fun sendToggleMessage(playerEntity: PlayerEntity, value: Boolean, option: Text?) {
        playerEntity.sendMessage(
            Text.translatable(
                "too_many_shortcuts.toggled." + (if (value) "on" else "off"),
                option
            ), true
        )
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
