package dev.kingtux.tms.keybinding.shortcuts

import dev.kingtux.tms.api.TMSKeyBinding
import dev.kingtux.tms.api.modifiers.BindingModifiers
import dev.kingtux.tms.keybinding.TMSExtraShortcuts
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
        TMSExtraShortcuts.sendToggleMessage(
            minecraftClient.player!!,
            autoJump,
            Text.translatable("too_many_shortcuts.toggled.auto_jump")
        )
    }
}
