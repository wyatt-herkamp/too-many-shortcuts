package dev.kingtux.tms.gui

import dev.kingtux.tms.api.translatedTextEqualsIgnoreCase
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.option.KeyBinding
import net.minecraft.text.Text
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import java.util.*

interface ControlsListWidget<Self : ControlsListWidget<Self, E, T>, E : KeyBindingEntry<Self>, T : KeyBindingScreenType<T, E, Self>> {
    var maxKeyNameLength: Int

    val parent: T
    fun createEntry(
        binding: KeyBinding,
        alternative: Boolean
    ): E

    fun createAllEntries(searchValue: String?): Pair<List<E>, Int> {
        val entries = mutableListOf<E>()
        val keyBindings = ArrayUtils.clone(parent.gameOptions().allKeys as Array<KeyBinding>)
        var maxKeyNameLength = 0;
        Arrays.sort(keyBindings)
        for (keyBinding in keyBindings) {
            val bindingAsIKeyBinding = keyBinding as IKeyBinding
            if (bindingAsIKeyBinding.`tms$isAlternative`()) {
                continue;
            }

            var shouldAdd = parent.show.doesKeyBindingMatchRequirements(keyBinding, parent.client().options);
            if (!shouldAdd) {
                continue;
            }
            if (!searchValue.isNullOrEmpty()) {
                shouldAdd =
                    if (StringUtils.containsIgnoreCase(Text.translatable(keyBinding.category.id.toTranslationKey("key.category")).string, searchValue)) {
                        true
                    } else {
                        keyBinding.translatedTextEqualsIgnoreCase(searchValue)
                    }
            }
            if (!shouldAdd) {
                continue;
            }
            val entry = createEntry(keyBinding, false)

            val textWidth = entry.getWidth(parent.client().textRenderer)
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