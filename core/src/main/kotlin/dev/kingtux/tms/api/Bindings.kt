package dev.kingtux.tms.api

import dev.kingtux.tms.api.modifiers.BindingModifiers
import com.mojang.blaze3d.platform.InputConstants

data class DefaultBindings(
    val primary: Binding,
    val alternatives: List<Binding>
) {
    constructor(primary: Binding) : this(primary, emptyList())
    constructor(key: InputConstants.Key) : this(Binding(key, BindingModifiers()))
}

data class Binding(
    val key: InputConstants.Key,
    val modifiers: BindingModifiers
) {
    constructor(key: InputConstants.Key) : this(key, BindingModifiers())
}