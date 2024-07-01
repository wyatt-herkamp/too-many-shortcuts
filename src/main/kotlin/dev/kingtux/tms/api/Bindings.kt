package dev.kingtux.tms.api

import dev.kingtux.tms.api.modifiers.BindingModifiers
import net.minecraft.client.util.InputUtil

data class DefaultBindings(
    val primary: Binding,
    val alternatives: List<Binding>
) {
    constructor(primary: Binding) : this(primary, emptyList())
    constructor(key: InputUtil.Key) : this(Binding(key, BindingModifiers()))
}

data class Binding(
    val key: InputUtil.Key,
    val modifiers: BindingModifiers
) {
    constructor(key: InputUtil.Key) : this(key, BindingModifiers())
}