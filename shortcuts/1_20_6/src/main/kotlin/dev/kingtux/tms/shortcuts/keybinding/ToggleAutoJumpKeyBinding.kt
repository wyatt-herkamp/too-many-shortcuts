package dev.kingtux.tms.shortcuts.keybinding

import dev.kingtux.tms.shortcuts.TmsShortcuts.sendToggleMessage
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
) : TMSKeyBinding(id, type, code, category, defaultModifiers){
    override fun onPressed() {
        val minecraftClient = MinecraftClient.getInstance()
        val autoJump = !minecraftClient.options.autoJump.value
        minecraftClient.options.autoJump.value = autoJump
        sendToggleMessage(
            minecraftClient.player!!,
            autoJump,
            Text.translatable("too_many_shortcuts.toggled.auto_jump")
        )
    }
}
