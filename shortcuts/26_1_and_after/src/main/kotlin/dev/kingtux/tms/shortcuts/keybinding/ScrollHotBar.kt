package dev.kingtux.tms.shortcuts.keybinding

import dev.kingtux.tms.api.TMSKeyBinding
import dev.kingtux.tms.api.modifiers.BindingModifiers
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.resources.Identifier

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
    type: InputConstants.Type?,
    code: Int,
    category: Category?,
    defaultModifiers: BindingModifiers?,

    private val direction: HotBarDirection
) : TMSKeyBinding(id, type, code, category, defaultModifiers) {
    override fun onPressed() {
        val client = Minecraft.getInstance()
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
