@file:Suppress("CAST_NEVER_SUCCEEDS")

package dev.kingtux.tms.alternatives

import dev.kingtux.tms.config.ConfigBindings
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

    constructor(parent: KeyBinding, config: ConfigBindings) : this(
        parent,
        alternativeKeyBindingTranslationKey(parent),
        InputUtil.UNKNOWN_KEY
    ) {
        this.boundKey = InputUtil.fromTranslationKey(config.key)
        (this as IKeyBinding).`tms$setKeyModifiers`(config.modifiers)
    }

    override fun isDefault(): Boolean {
        return this.defaultKey == this.boundKey
    }

    override fun setPressed(pressed: Boolean) {
        //println("Setting pressed state for AlternativeKeyBinding: $pressed")
        super.setPressed(pressed)
        (this as IKeyBinding).`tms$getParent`()?.isPressed = pressed
    }
    override fun toString(): String {
        return "AlternativeKeyBinding{parent=${this.translationKey}, key=${this.boundKey}, modifiers=${(this as IKeyBinding).`tms$getKeyModifiers`()}}"
    }
}