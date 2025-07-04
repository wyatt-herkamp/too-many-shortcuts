package dev.kingtux.tms.api.modifiers

import dev.kingtux.tms.api.ModifierPrefixTextProvider
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.InputUtil
import org.apache.commons.lang3.ArrayUtils
/**
* the order of the enums makes a difference when generating the shown name in the gui
* with this order the old text order is preserved. But now the id values do not increment nicely. But changing them would eliminate
* backward compatibility with the old save format
*/
@Environment(EnvType.CLIENT)
enum class KeyModifier(
    val id: Int, val bit: Int, // these keyCodes are all from Type: InputUtil.Type.KEYSYM
    vararg val keyCodes: Int
) {
    /**
     * Alt key modifier.
     */
    ALT(0, 0x0004, 342, 346),

    /**
     * Shift key modifier.
     */
    SHIFT(2, 0x0001, 340, 344),

    /**
     * Control key modifier.
     */
    CONTROL(1, 0x0002, 341, 345);

    /**
     * The name of the modifier, used for translation keys and display names.
     * This is the same as the enum name but in lowercase.
     */
    val textProvider: ModifierPrefixTextProvider =
        ModifierPrefixTextProvider(this)

    /**
     * Checks if the given keyCode matches this KeyModifier.
     */
    fun matches(keyCode: Int): Boolean {
        return ArrayUtils.contains(keyCodes, keyCode)
    }

    /**
     * Returns the translation key for this KeyModifier.
     * The translation key is in the format "tms.modifier.{name}" where {name} is the lowercase name of the modifier.
     */
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

        /**
         * Returns the KeyModifier for a given keyCode.
         * If no KeyModifier matches the keyCode, returns null.
         *
         * @param keyCode The key code to check.
         *
         * @return The KeyModifier that matches the keyCode, or null if none match.
         */
        fun fromKeyCode(keyCode: Int): KeyModifier? {
            for (keyModifier in entries) {
                if (keyModifier.matches(keyCode)) {
                    return keyModifier
                }
            }
            return null
        }

        /**
         * Checks if the given key is a key modifier (ALT, SHIFT, CONTROL).
         *
         * @param key The InputUtil.Key to check.
         *
         * @return True if the key is a key modifier, false otherwise.
         */
        fun isKeyModifier(key: InputUtil.Key?): Boolean {
            if (key == null || key.category != InputUtil.Type.KEYSYM) {
                return false
            }
            for (keyModifier in entries) {
                if (keyModifier.matches(key.code)) {
                    return true
                }
            }
            return false
        }

        /**
         * Returns the KeyModifier for a given InputUtil.Key.
         *
         * If the key is null or not a key symbol, returns null.
         *
         * @param key The InputUtil.Key to check.
         * @return The KeyModifier that matches the key, or null if none match.
         */
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
