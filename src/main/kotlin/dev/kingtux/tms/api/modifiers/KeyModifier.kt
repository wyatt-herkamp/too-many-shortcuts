package dev.kingtux.tms.api.modifiers

import dev.kingtux.tms.api.ModifierPrefixTextProvider
import dev.kingtux.tms.TooManyShortcuts
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.InputUtil
import org.apache.commons.lang3.ArrayUtils

@Environment(EnvType.CLIENT)
enum class KeyModifier(val id: Int, vararg keyCodes: Int) {
    // the order of the enums makes a difference when generating the shown name in the gui
    // with this order the old text order is preserved. But now the id values do not increment nicely. But changing them would eliminate
    // backward compatibility with the old save format
    NONE(-1),
    ALT(0, 342, 346),
    SHIFT( 2, 340, 344),
    CONTROL( 1, 341, 345);

    val textProvider: ModifierPrefixTextProvider =
        ModifierPrefixTextProvider(this)

    // these keyCodes are all from Type: InputUtil.Type.KEYSYM
    val keyCodes: IntArray

    init {
        this.keyCodes = keyCodes
    }

    fun matches(keyCode: Int): Boolean {
        return ArrayUtils.contains(keyCodes, keyCode)
    }

    val translationKey: String
        get() = TooManyShortcuts.MOD_ID + ".modifier." + name.lowercase()

    companion object {
        // using this array for the values because it is faster than calling values() every time
        val VALUES: Array<KeyModifier> = entries.toTypedArray()

        fun fromKeyCode(keyCode: Int): KeyModifier {
            for (keyModifier in VALUES) {
                if (keyModifier == NONE) {
                    continue
                }
                if (keyModifier.matches(keyCode)) {
                    return keyModifier
                }
            }
            return NONE
        }

        fun fromKey(key: InputUtil.Key?): KeyModifier {
            if (key == null || key.category != InputUtil.Type.KEYSYM) {
                return NONE
            }
            return fromKeyCode(key.code)
        }

        val modifierCount: Int
            get() = VALUES.size - 1 // remove 1 for NONE
    }
}
