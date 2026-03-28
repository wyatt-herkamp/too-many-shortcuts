package dev.kingtux.tms.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.Options

interface KeyBindingScreenType<Self : KeyBindingScreenType<Self, T, W>, T : KeyBindingEntry<W>, W : ControlsListWidget<W, T, Self>> {
    var selectedKeyBinding: T?
    var lastKeyCodeUpdateTime: Long
    var isShiftEnabled: Boolean
    var show: ShowOptions
    var screenMode: ScreenModes
    fun setSelectedKeyBindingToNull() {
        selectedKeyBinding = null
    }

    fun gameOptions(): Options

    fun client(): Minecraft

    fun isSelected(entry: T): Boolean{
        return selectedKeyBinding == entry
    }
}