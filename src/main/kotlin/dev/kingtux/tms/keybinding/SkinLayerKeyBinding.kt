package dev.kingtux.tms.keybinding

import dev.kingtux.tms.TooManyShortcuts.sendToggleMessage
import dev.kingtux.tms.api.TMSKeyBinding
import dev.kingtux.tms.api.modifiers.BindingModifiers
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.player.PlayerModelPart
import net.minecraft.util.Identifier

class SkinLayerKeyBinding(
    id: Identifier?,
    type: InputUtil.Type?,
    code: Int,
    category: String?,
    private val playerModelPart: PlayerModelPart
) : TMSKeyBinding(id, type, code, category, BindingModifiers()) {
    override fun onPressed() {
        val client = MinecraftClient.getInstance()
        if (client.player == null) return
        // TODO: Figure out how to do this in 1.21.3
        //client.options.togglePlayerModelPart(playerModelPart, !client.options.isPlayerModelPartEnabled(playerModelPart))
        sendToggleMessage(
            client.player!!,
            client.options.isPlayerModelPartEnabled(playerModelPart),
            playerModelPart.optionName
        )
    }
}
