package dev.kingtux.tms.compat

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen

/**
 * Cross-version access to the current [Screen], which moved between Minecraft versions:
 *  - 26.1 (and earlier): the `screen` field and `setScreen` live on [Minecraft].
 *  - 26.2 (and later): they moved to the `Gui` class, reached via `Minecraft.gui`.
 *
 * This module compiles against 26.2, so the modern path uses direct calls
 * (`mc.gui.screen()` / `mc.gui.setScreen(...)`) and the legacy path uses reflection
 * (`Minecraft.screen` / `Minecraft.setScreen`). [legacy] is resolved once: the JVM only
 * links the bytecode of whichever branch actually executes, so the modern direct calls
 * are never resolved when running on 26.1 and vice-versa.
 */
object ClientCompat {
    /** True when running on a Minecraft that still exposes `Minecraft.screen` (26.1 and earlier). */
    private val legacy: Boolean = runCatching {
        Minecraft::class.java.getField("screen")
        true
    }.getOrDefault(false)

    private val legacyScreenField by lazy(LazyThreadSafetyMode.PUBLICATION) {
        Minecraft::class.java.getField("screen")
    }
    private val legacySetScreen by lazy(LazyThreadSafetyMode.PUBLICATION) {
        Minecraft::class.java.getMethod("setScreen", Screen::class.java)
    }

    /** The currently displayed screen, or null if none. */
    @JvmStatic
    fun getScreen(mc: Minecraft): Screen? =
        if (legacy) legacyScreenField.get(mc) as Screen?
        else mc.gui.screen()

    /** Replace the currently displayed screen. */
    @JvmStatic
    fun setScreen(mc: Minecraft, screen: Screen) {
        if (legacy) legacySetScreen.invoke(mc, screen)
        else mc.gui.setScreen(screen)
    }
}
