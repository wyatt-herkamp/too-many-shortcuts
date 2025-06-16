package dev.kingtux.tms.gui

import net.minecraft.client.option.KeyBinding

interface KeyBindingScreenType {
    var lastKeyCodeUpdateTime: Long

    fun setSelectedKeyBindingToNull()
}