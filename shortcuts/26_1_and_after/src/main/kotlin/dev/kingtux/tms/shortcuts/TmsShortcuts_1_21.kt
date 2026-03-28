package dev.kingtux.tms.shortcuts

import dev.kingtux.tms.api.TMSKeyBinding
import dev.kingtux.tms.api.modifiers.BindingModifiers
import dev.kingtux.tms.shortcuts.keybinding.HotBarDirection
import dev.kingtux.tms.shortcuts.keybinding.ScrollHotBar
import dev.kingtux.tms.shortcuts.keybinding.SkinLayerKeyBinding
import dev.kingtux.tms.shortcuts.keybinding.ToggleAutoJumpKeyBinding
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.world.entity.player.PlayerModelPart
import net.minecraft.resources.Identifier
import java.util.*

object TmsShortcuts_1_21 : ClientModInitializer {
    override fun onInitializeClient() {
        TmsShortcuts.setEscapeKeyBinding(
            KeyMappingHelper.registerKeyMapping(
                TMSKeyBinding(
                    Identifier.fromNamespaceAndPath(TmsShortcuts.MOD_ID, "alternative_escape"),
                    InputConstants.Type.KEYSYM,
                    InputConstants.UNKNOWN.value,
                    KeyMapping.Category.MISC,
                    BindingModifiers()
                )
            )
        )
        KeyMappingHelper.registerKeyMapping(
            ToggleAutoJumpKeyBinding(
                Identifier.fromNamespaceAndPath(TmsShortcuts.MOD_ID, "toggle_auto_jump"),
                InputConstants.Type.KEYSYM,
                66,
                KeyMapping.Category.MISC,
                BindingModifiers()
            )
        )


        HotBarDirection.entries.map {
            ScrollHotBar(
                Identifier.fromNamespaceAndPath(TmsShortcuts.MOD_ID, it.bindingKey()),
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                KeyMapping.Category.INVENTORY,
                BindingModifiers(),
                it
            )
        }
            .forEach { keyBinding: TMSKeyBinding ->
                KeyMappingHelper.registerKeyMapping(keyBinding)
            }


        Arrays.stream(PlayerModelPart.entries.toTypedArray())
            .map { playerModelPart: PlayerModelPart ->
                SkinLayerKeyBinding(
                    Identifier.fromNamespaceAndPath(TmsShortcuts.MOD_ID, "toggle_" + playerModelPart.name.lowercase()),
                    InputConstants.Type.KEYSYM,
                    InputConstants.UNKNOWN.value,
                    TmsShortcuts.SKIN_LAYER_CATEGORY,
                    playerModelPart
                )
            }
            .forEach { keyBinding: SkinLayerKeyBinding ->
                KeyMappingHelper.registerKeyMapping(
                    keyBinding
                )
            }
    }


}