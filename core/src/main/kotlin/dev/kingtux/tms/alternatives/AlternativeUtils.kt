package dev.kingtux.tms.alternatives

import dev.kingtux.tms.api.modifiers.BindingModifiers
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.KeyMapping

fun resetSingleKeyBinding(keyBinding: KeyMapping) {
    keyBinding.setKey(keyBinding.defaultKey)
    if (keyBinding is IKeyBinding) {
        (keyBinding as IKeyBinding).`tms$setKeyModifiers`(BindingModifiers())
    }
}

fun alternativeKeyBindingTranslationKey(base: KeyMapping): String {
    if (base !is IKeyBinding) {
        throw IllegalArgumentException("Parent keybinding must implement IKeyBinding")
    }
    return base.name + "%" + base.`tms$getNextChildId`()
}
