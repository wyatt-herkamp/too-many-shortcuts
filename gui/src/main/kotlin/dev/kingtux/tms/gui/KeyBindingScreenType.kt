package dev.kingtux.tms.gui

import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.GameOptions

interface KeyBindingScreenType<Self : KeyBindingScreenType<Self, T, W>, T : KeyBindingEntry<W>, W : ControlsListWidget<W, T, Self>> {
    var selectedKeyBinding: T?
    var lastKeyCodeUpdateTime: Long
    var isShiftEnabled: Boolean
    var show: ShowOptions
    var screenMode: ScreenModes
    fun setSelectedKeyBindingToNull() {
        selectedKeyBinding = null
    }

    fun gameOptions(): GameOptions

    fun client(): MinecraftClient
}