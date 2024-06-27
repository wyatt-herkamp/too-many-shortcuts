package dev.kingtux.tms

import de.siphalor.amecs.api.AmecsKeyBinding
import de.siphalor.amecs.api.KeyModifiers
import de.siphalor.amecs.keybinding.SkinLayerKeyBinding
import de.siphalor.amecs.keybinding.ToggleAutoJumpKeyBinding
import de.siphalor.amecs.mixin.ControlsListWidgetKeyBindingEntryAccessor
import de.siphalor.api.impl.duck.IKeyBindingEntry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.gui.screen.option.ControlsListWidget
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerModelPart
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

    val CURRENT_MODIFIERS: KeyModifiers = KeyModifiers()

    val ESCAPE_KEYBINDING: KeyBinding = KeyBindingHelper.registerKeyBinding(
        AmecsKeyBinding(
            Identifier.of(MOD_ID, "alternative_escape"),
            InputUtil.Type.KEYSYM,
            -1,
            "key.categories.ui",
            KeyModifiers()
        )
    )

    override fun onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(
            ToggleAutoJumpKeyBinding(
                Identifier.of(MOD_ID, "toggle_auto_jump"),
                InputUtil.Type.KEYSYM,
                66,
                "key.categories.movement",
                KeyModifiers()
            )
        )

        Arrays.stream(PlayerModelPart.entries.toTypedArray())
            .map { playerModelPart: PlayerModelPart ->
                SkinLayerKeyBinding(
                    Identifier.of(MOD_ID, "toggle_" + playerModelPart.getName().lowercase()),
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
        playerEntity.sendMessage(Text.translatable("too_many_shortcuts.toggled." + (if (value) "on" else "off"), option), true)
    }

    fun entryKeyMatches(entry: ControlsListWidget.KeyBindingEntry, keyFilter: String?): Boolean {
        if (keyFilter == null) {
            return true
        }
        return when (keyFilter) {
            "" -> (entry as IKeyBindingEntry).`amecs$getKeyBinding`().isUnbound
            "%" -> (entry as ControlsListWidgetKeyBindingEntryAccessor).editButton.message.style.color === TextColor.fromFormatting(
                Formatting.RED
            )

            else -> StringUtils.containsIgnoreCase(
                (entry as IKeyBindingEntry).`amecs$getKeyBinding`().boundKeyLocalizedText.string,
                keyFilter
            )
        }
    }
    fun getCurrentModifiers(): KeyModifiers {
        return CURRENT_MODIFIERS
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