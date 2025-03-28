@file:Suppress("CAST_NEVER_SUCCEEDS")

package dev.kingtux.tms.alternatives

import dev.kingtux.tms.api.ConfigBindings
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class AlternativeKeyBinding(
    parent: KeyBinding,
    translationKey: String,
    code: InputUtil.Key,
) : KeyBinding(
    translationKey,
    code.category,
    code.code,
    parent.category
) {
    init {
        if (parent !is IKeyBinding) {
            throw IllegalArgumentException("Parent keybinding must implement IKeyBinding")
        }
        (parent as IKeyBinding).`tms$addAlternative`(this)
        (this as IKeyBinding).`tms$setParent`(parent)
    }

    constructor(parent: KeyBinding) : this(parent, alternativeKeyBindingTranslationKey(parent));

    constructor(
        parent: KeyBinding,
        translationKey: String,
    ) : this(parent, translationKey, InputUtil.UNKNOWN_KEY);

    constructor(parent: KeyBinding, config: ConfigBindings ) : this(
        parent,
        alternativeKeyBindingTranslationKey(parent),
        InputUtil.UNKNOWN_KEY
    ) {
        (this as IKeyBinding).`tms$setBoundKey`( InputUtil.fromTranslationKey(config.key))
        (this as IKeyBinding).`tms$setKeyModifiers`(config.modifiers)
    }

    override fun isDefault(): Boolean {
        return this.defaultKey == (this as IKeyBinding).`tms$getBoundKey`()
    }

    override fun toString(): String {
        return "AlternativeKeyBinding{parent=${this.translationKey}, key=${(this as IKeyBinding).`tms$getBoundKey`()}, modifiers=${(this as IKeyBinding).`tms$getKeyModifiers`()}}"
    }
}