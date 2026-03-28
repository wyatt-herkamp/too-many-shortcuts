@file:Suppress("CAST_NEVER_SUCCEEDS")

package dev.kingtux.tms.alternatives

import dev.kingtux.tms.api.config.ConfigBindings
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.KeyMapping
import com.mojang.blaze3d.platform.InputConstants
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class AlternativeKeyBinding(
    parent: KeyMapping,
    translationKey: String,
    code: InputConstants.Key,
) : KeyMapping(
    translationKey,
    code.type,
    code.value,
    parent.category
) {
    init {
        if (parent !is IKeyBinding) {
            throw IllegalArgumentException("Parent keybinding must implement IKeyBinding")
        }
        (parent as IKeyBinding).`tms$addAlternative`(this)
        (this as IKeyBinding).`tms$setParent`(parent)
    }

    constructor(parent: KeyMapping) : this(parent, alternativeKeyBindingTranslationKey(parent));

    constructor(
        parent: KeyMapping,
        translationKey: String,
    ) : this(parent, translationKey, InputConstants.UNKNOWN);

    constructor(parent: KeyMapping, config: ConfigBindings) : this(
        parent,
        alternativeKeyBindingTranslationKey(parent),
        InputConstants.UNKNOWN
    ) {
        (this as IKeyBinding).`tms$setBoundKey`(InputConstants.getKey(config.key))
        (this as IKeyBinding).`tms$setKeyModifiers`(config.modifiers)
    }

    override fun isDefault(): Boolean {
        return (this.defaultKey == (this as IKeyBinding).`tms$getBoundKey`())
    }

    override fun setDown(pressed: Boolean) {
        //println("Setting pressed state for AlternativeKeyBinding: $pressed")
        super.setDown(pressed)
        (this as IKeyBinding).`tms$getParent`()?.isDown = pressed
    }

    override fun toString(): String {
        return "AlternativeKeyBinding{parent=${this.name}, key=${(this as IKeyBinding).`tms$getBoundKey`()}, modifiers=${(this as IKeyBinding).`tms$getKeyModifiers`()}}"
    }
}