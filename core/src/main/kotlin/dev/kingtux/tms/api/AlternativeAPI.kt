package dev.kingtux.tms.api

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.option.KeyBinding
import java.util.HashMap

object AlternativeAPI {
    val defaultAlternatives: Multimap<KeyBinding, KeyBinding> = Multimaps.newSetMultimap(HashMap()) { hashSetOf() };


    /**
     * Returns whether the given keybinding is an alternative.
     *
     * @param binding A keybinding
     * @return Whether the given keybinding is an alternative
     */
    fun isAlternative(binding: KeyBinding): Boolean {
        return (binding as IKeyBinding).`tms$isAlternative`()
    }

    /**
     * Gets all alternatives that are registered for a keybinding.
     *
     * @param binding A keyinding
     * @return A list of alternatives or `null`
     */
    fun getAlternatives(binding: KeyBinding): List<KeyBinding>? {
        return (binding as IKeyBinding).`tms$getAlternatives`()
    }

    /**
     * Gets the base keybinding for an alternative keybinding.
     *
     * @param binding An alternative keybinding
     * @return The base keyinding or `null` if the given keybinding is no alternative
     */
    fun getBase(binding: KeyBinding): KeyBinding? {
        return (binding as IKeyBinding).`tms$getParent`()
    }
}