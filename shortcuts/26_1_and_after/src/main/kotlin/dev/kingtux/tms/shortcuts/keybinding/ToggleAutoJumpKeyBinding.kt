package dev.kingtux.tms.shortcuts.keybinding

import dev.kingtux.tms.api.TMSKeyBinding
import dev.kingtux.tms.api.modifiers.BindingModifiers
import dev.kingtux.tms.shortcuts.TmsShortcuts.sendToggleMessage
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

class ToggleAutoJumpKeyBinding(
    id: Identifier?,
    type: InputConstants.Type?,
    code: Int,
    category: Category?,
    defaultModifiers: BindingModifiers?
) : TMSKeyBinding(id, type, code, category, defaultModifiers) {
    override fun onPressed() {
        val minecraftClient = Minecraft.getInstance()
        val autoJump = !minecraftClient.options.autoJump().get()
        minecraftClient.options.autoJump().set(autoJump)
        sendToggleMessage(
            minecraftClient.player!!,
            autoJump,
            Component.translatable("too_many_shortcuts.toggled.auto_jump")
        )
    }
}
