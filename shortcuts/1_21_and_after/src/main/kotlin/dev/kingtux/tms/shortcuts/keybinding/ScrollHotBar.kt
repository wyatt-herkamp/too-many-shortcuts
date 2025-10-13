package dev.kingtux.tms.shortcuts.keybinding

import dev.kingtux.tms.api.TMSKeyBinding
import dev.kingtux.tms.api.modifiers.BindingModifiers
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier

enum class HotBarDirection {
    LEFT, RIGHT;

    fun bindingKey(): String {
        return when (this) {
            LEFT -> "inventory.scroll_hotbar.left"
            RIGHT -> "inventory.scroll_hotbar.right"
        }
    }
}

class ScrollHotBar(
    id: Identifier?,
    type: InputUtil.Type?,
    code: Int,
    category: Category?,
    defaultModifiers: BindingModifiers?,

    private val direction: HotBarDirection
) : TMSKeyBinding(id, type, code, category, defaultModifiers) {
    override fun onPressed() {
        val client = MinecraftClient.getInstance()
        if (client.player == null) return
        // Set hotbar slot based on direction
        val currentSlot = client.player?.inventory?.selectedSlot ?: 0
        val newSlot = when (direction) {
            HotBarDirection.LEFT -> (currentSlot - 1 + 9) % 9 // Wrap around to the end
            HotBarDirection.RIGHT -> (currentSlot + 1) % 9 // Wrap around to the start
        }
        client.player?.inventory?.selectedSlot = newSlot
    }
}
