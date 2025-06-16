package dev.kingtux.tms.api.modifiers

import dev.kingtux.tms.api.ModifierPrefixTextProvider
import dev.kingtux.tms.TooManyShortcutsCore
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.InputUtil
import org.apache.commons.lang3.ArrayUtils

@Environment(EnvType.CLIENT)
enum class KeyModifier(
    val id: Int, val bit: Int, // these keyCodes are all from Type: InputUtil.Type.KEYSYM
    vararg val keyCodes: Int
) {
    // the order of the enums makes a difference when generating the shown name in the gui
    // with this order the old text order is preserved. But now the id values do not increment nicely. But changing them would eliminate
    // backward compatibility with the old save format
    ALT(0, 0x0004, 342, 346),
    SHIFT(2, 0x0001, 340, 344),
    CONTROL(1, 0x0002, 341, 345);


    val textProvider: ModifierPrefixTextProvider =
        ModifierPrefixTextProvider(this)

    fun matches(keyCode: Int): Boolean {
        return ArrayUtils.contains(keyCodes, keyCode)
    }

    val translationKey: String
        get() = "tms.modifier." + name.lowercase()

    companion object {
        // using this array for the values because it is faster than calling values() every time
        fun fromModifiers(modifiers: Int): List<KeyModifier> {
            val result: MutableList<KeyModifier> = ArrayList()
            for (keyModifier in entries) {
                if ((modifiers and keyModifier.bit) == keyModifier.bit) {
                    result.add(keyModifier)
                }
            }
            return result
        }

        fun fromKeyCode(keyCode: Int): KeyModifier? {
            for (keyModifier in entries) {
                if (keyModifier.matches(keyCode)) {
                    return keyModifier
                }
            }
            return null
        }
        fun isKeyModifier(key: InputUtil.Key?): Boolean {
            return fromKey(key) != null
        }
        fun fromKey(key: InputUtil.Key?): KeyModifier? {
            if (key == null || key.category != InputUtil.Type.KEYSYM) {
                return null
            }
            return fromKeyCode(key.code)
        }

        val modifierCount: Int
            get() = entries.size - 1 // remove 1 for NONE
    }
}
