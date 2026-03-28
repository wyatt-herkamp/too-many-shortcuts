package dev.kingtux.tms.api

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.KeyMapping

object AlternativeAPI {
    val defaultAlternatives: Multimap<KeyMapping, KeyMapping> = Multimaps.newSetMultimap(HashMap()) { hashSetOf() };


    /**
     * Returns whether the given keybinding is an alternative.
     *
     * @param binding A keybinding
     * @return Whether the given keybinding is an alternative
     */
    fun isAlternative(binding: KeyMapping): Boolean {
        return (binding as IKeyBinding).`tms$isAlternative`()
    }

    /**
     * Gets all alternatives that are registered for a keybinding.
     *
     * @param binding A keyinding
     * @return A list of alternatives or `null`
     */
    fun getAlternatives(binding: KeyMapping): List<KeyMapping>? {
        return (binding as IKeyBinding).`tms$getAlternatives`()
    }

    /**
     * Gets the base keybinding for an alternative keybinding.
     *
     * @param binding An alternative keybinding
     * @return The base keyinding or `null` if the given keybinding is no alternative
     */
    fun getBase(binding: KeyMapping): KeyMapping? {
        return (binding as IKeyBinding).`tms$getParent`()
    }
}