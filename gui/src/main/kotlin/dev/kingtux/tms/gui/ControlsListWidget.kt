package dev.kingtux.tms.gui

import dev.kingtux.tms.api.entryKeyMatches
import dev.kingtux.tms.api.translatedTextEqualsIgnoreCase
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.KeyMapping
import net.minecraft.network.chat.Component
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import java.util.*

interface ControlsListWidget<Self : ControlsListWidget<Self, E, T>, E : KeyBindingEntry<Self>, T : KeyBindingScreenType<T, E, Self>> {
    var maxKeyNameLength: Int

    val parent: T
    fun createEntry(
        binding: KeyMapping,
        alternative: Boolean
    ): E

    fun createAllEntries(searchValue: String?): Pair<List<E>, Int> {
        val entries = mutableListOf<E>()
        val keyBindings = ArrayUtils.clone(parent.gameOptions().keyMappings as Array<KeyMapping>)
        var maxKeyNameLength = 0
        Arrays.sort(keyBindings)

        val isKeySearch = searchValue != null && searchValue.startsWith("=")
        val keySearchTerm = if (isKeySearch) searchValue!!.substring(1) else null

        for (keyBinding in keyBindings) {
            val bindingAsIKeyBinding = keyBinding as IKeyBinding
            if (bindingAsIKeyBinding.`tms$isAlternative`()) {
                continue
            }

            if (!parent.show.doesKeyBindingMatchRequirements(keyBinding, parent.client().options)) {
                continue
            }

            if (isKeySearch) {
                val parentMatches = keyBinding.entryKeyMatches(keySearchTerm)
                val anyAltMatches = if (bindingAsIKeyBinding.`tms$hasAlternatives`()) {
                    bindingAsIKeyBinding.`tms$getAlternatives`().any { it.entryKeyMatches(keySearchTerm) }
                } else false
                if (!parentMatches && !anyAltMatches) continue
            } else if (!searchValue.isNullOrEmpty()) {
                val matchesCategory = StringUtils.containsIgnoreCase(keyBinding.category.label().getString(), searchValue)
                val matchesName = keyBinding.translatedTextEqualsIgnoreCase(searchValue)
                if (!matchesCategory && !matchesName) continue
            }

            val entry = createEntry(keyBinding, false)
            val textWidth = entry.getWidth(parent.client().font)
            if (textWidth > maxKeyNameLength) {
                maxKeyNameLength = textWidth
            }
            entries.add(entry)
            // By Skipping the alternatives we can add them directly after the parent entry. This ensures that they are in the correct order.
            if (bindingAsIKeyBinding.`tms$hasAlternatives`()) {
                for (alternative in bindingAsIKeyBinding.`tms$getAlternatives`()) {
                    entries.add(createEntry(alternative, true))
                }
            }
        }
        return Pair(entries, maxKeyNameLength)
    }
}