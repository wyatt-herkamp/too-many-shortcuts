package dev.kingtux.tms.alternatives

import dev.kingtux.tms.api.modifiers.BindingModifiers
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.option.KeyBinding

fun resetSingleKeyBinding(keyBinding: KeyBinding) {
    keyBinding.setBoundKey(keyBinding.defaultKey)
    if (keyBinding is IKeyBinding) {
        (keyBinding as IKeyBinding).`tms$setKeyModifiers`(BindingModifiers())
    }
}

fun alternativeKeyBindingTranslationKey(base: KeyBinding): String {
    if (base !is IKeyBinding) {
        throw IllegalArgumentException("Parent keybinding must implement IKeyBinding")
    }
    return base.id + "%" + base.`tms$getNextChildId`()
}
