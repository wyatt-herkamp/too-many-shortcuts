package dev.kingtux.tms.keybinding

import dev.kingtux.tms.TooManyShortcuts.sendToggleMessage
import dev.kingtux.tms.api.TMSKeyBinding
import dev.kingtux.tms.api.modifiers.BindingModifiers
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class ToggleAutoJumpKeyBinding(
    id: Identifier?,
    type: InputUtil.Type?,
    code: Int,
    category: String?,
    defaultModifiers: BindingModifiers?
) : TMSKeyBinding(id, type, code, category, defaultModifiers) {
    override fun onPressed() {
        val minecraftClient = MinecraftClient.getInstance()
        val autoJump = !minecraftClient.options.autoJump.value
        minecraftClient.options.autoJump.value = autoJump
        sendToggleMessage(minecraftClient.player!!, autoJump, Text.translatable("amecs.toggled.auto_jump"))
    }
}
