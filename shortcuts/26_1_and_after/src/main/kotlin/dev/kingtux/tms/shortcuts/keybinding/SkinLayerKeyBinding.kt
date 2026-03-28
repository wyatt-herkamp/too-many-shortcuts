package dev.kingtux.tms.shortcuts.keybinding

import dev.kingtux.tms.api.TMSKeyBinding
import dev.kingtux.tms.api.modifiers.BindingModifiers
import dev.kingtux.tms.shortcuts.TmsShortcuts.sendToggleMessage
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.world.entity.player.PlayerModelPart
import net.minecraft.resources.Identifier

class SkinLayerKeyBinding(
    id: Identifier?,
    type: InputConstants.Type?,
    code: Int,
    category: Category?,
    private val playerModelPart: PlayerModelPart
) : TMSKeyBinding(id, type, code, category, BindingModifiers()) {
    override fun onPressed() {
        val client = Minecraft.getInstance()
        if (client.player == null) return
        client.options.setModelPart(playerModelPart, !client.options.isModelPartEnabled(playerModelPart))
        sendToggleMessage(
            client.player!!,
            client.options.isModelPartEnabled(playerModelPart),
            playerModelPart.getName()
        )
    }
}
