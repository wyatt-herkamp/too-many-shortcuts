package dev.kingtux.tms.api

import dev.kingtux.tms.api.modifiers.BindingModifiers

object TMSKeyBindingManager {
    val currentModifiers: BindingModifiers = BindingModifiers()
    val scrollBindings: Boolean = true
}