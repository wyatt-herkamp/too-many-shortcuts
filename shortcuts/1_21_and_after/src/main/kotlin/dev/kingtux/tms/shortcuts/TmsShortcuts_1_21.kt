package dev.kingtux.tms.shortcuts

import dev.kingtux.tms.api.TMSKeyBinding
import dev.kingtux.tms.api.modifiers.BindingModifiers
import dev.kingtux.tms.shortcuts.keybinding.HotBarDirection
import dev.kingtux.tms.shortcuts.keybinding.ScrollHotBar
import dev.kingtux.tms.shortcuts.keybinding.SkinLayerKeyBinding
import dev.kingtux.tms.shortcuts.keybinding.ToggleAutoJumpKeyBinding
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.player.PlayerModelPart
import net.minecraft.util.Identifier
import java.util.*

object TmsShortcuts_1_21 : ClientModInitializer {
    override fun onInitializeClient() {
        TmsShortcuts.setEscapeKeyBinding(
            KeyBindingHelper.registerKeyBinding(
                TMSKeyBinding(
                    Identifier.of(TmsShortcuts.MOD_ID, "alternative_escape"),
                    InputUtil.Type.KEYSYM,
                    -1,
                    "key.categories.ui",
                    BindingModifiers()
                )
            )
        )
        KeyBindingHelper.registerKeyBinding(
            ToggleAutoJumpKeyBinding(
                Identifier.of(TmsShortcuts.MOD_ID, "toggle_auto_jump"),
                InputUtil.Type.KEYSYM,
                66,
                "key.categories.movement",
                BindingModifiers()
            )
        )


        HotBarDirection.entries.map {
            ScrollHotBar(
                Identifier.of(TmsShortcuts.MOD_ID, it.bindingKey()),
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.code,
                "key.categories.inventory",
                BindingModifiers(),
                it
            )
        }
            .forEach { keyBinding: TMSKeyBinding ->
                KeyBindingHelper.registerKeyBinding(keyBinding)
            }


        Arrays.stream(PlayerModelPart.entries.toTypedArray())
            .map { playerModelPart: PlayerModelPart ->
                SkinLayerKeyBinding(
                    Identifier.of(TmsShortcuts.MOD_ID, "toggle_" + playerModelPart.name.lowercase()),
                    InputUtil.Type.KEYSYM,
                    -1,
                    TmsShortcuts.SKIN_LAYER_CATEGORY,
                    playerModelPart
                )
            }
            .forEach { keyBinding: SkinLayerKeyBinding? ->
                KeyBindingHelper.registerKeyBinding(
                    keyBinding
                )
            }
    }


}