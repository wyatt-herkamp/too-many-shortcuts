package dev.kingtux.tms.api.modifiers

import de.siphalor.amecs.api.KeyModifiers
import dev.kingtux.tms.TooManyShortcutsCore
import kotlinx.serialization.Serializable


@Serializable
data class BindingModifiers(
    var shift: Boolean = false,
    var ctrl: Boolean = false,
    var alt: Boolean = false,
) {
    /**
     * Checks if any of the modifiers (shift, ctrl, alt) are set.
     * @return true if at least one modifier is set, false otherwise.
     */
    fun hasModifiers(): Boolean {
        return shift || ctrl || alt
    }

    /**
     * Checks if all modifiers (shift, ctrl, alt) are unset.
     * @return true if none of the modifiers are set, false otherwise.
     */
    fun isUnset(): Boolean {
        return !shift && !ctrl && !alt
    }

    /**
     *  Clones the current instance of BindingModifiers.
     *  @return A new instance of BindingModifiers with the same values for shift, ctrl, and alt.
     */
    fun clone(): BindingModifiers {
        return BindingModifiers(shift, ctrl, alt)
    }
    /**
     * Unsets all modifiers (shift, ctrl, alt).
     */
    fun unset() {
        shift = false
        ctrl = false
        alt = false
    }
    /**
     * Checks if only one of the modifiers (shift, ctrl, alt) is set.
     * @return true if exactly one modifier is set, false otherwise.
     */
    fun onlyOneSet(): Boolean {
        return (shift && !ctrl && !alt) || (!shift && ctrl && !alt) || (!shift && !ctrl && alt)
    }
    /**
     * Checks if the specified modifier is set.
     * @param modifier The KeyModifier to check (SHIFT, CONTROL, or ALT).
     * @return true if the specified modifier is set, false otherwise.
     */
    fun isSet(modifier: KeyModifier): Boolean {
        return when (modifier) {
            KeyModifier.SHIFT -> shift
            KeyModifier.CONTROL -> ctrl
            KeyModifier.ALT -> alt
        }
    }
    /**
     * Sets the specified modifier to the given value.
     * @param modifier The KeyModifier to set (SHIFT, CONTROL, or ALT).
     * @param value The value to set the modifier to (true or false).
     */
    fun set(modifier: KeyModifier, value: Boolean) {
        when (modifier) {
            KeyModifier.SHIFT -> shift = value
            KeyModifier.CONTROL -> ctrl = value
            KeyModifier.ALT -> alt = value
        }
    }
    /**
     * Sets the modifiers based on the provided KeyModifiers instance.
     * @param modifiers The KeyModifiers instance to set the modifiers from.
     */
    fun set(modifiers: KeyModifiers) {
        shift = modifiers.shift
        ctrl = modifiers.control
        alt = modifiers.alt
    }
    /**
     * Sets the modifiers based on the provided BindingModifiers instance.
     * @param modifiers The BindingModifiers instance to set the modifiers from.
     */
    fun set(modifiers: BindingModifiers) {
        shift = modifiers.shift
        ctrl = modifiers.ctrl
        alt = modifiers.alt
    }

    /**
     * Checks if the current modifiers match the provided BindingModifiers.
     * @param boundModifiers The BindingModifiers to check against.
     */
    fun contains(boundModifiers: BindingModifiers): Boolean {
        return shift == boundModifiers.shift && ctrl == boundModifiers.ctrl && alt == boundModifiers.alt
    }
    /**
     * Checks if the current modifiers match the provided KeyModifiers.
     * @param boundModifiers The KeyModifiers to check against.
     */
    fun isPressed(): Boolean {
        return this == TooManyShortcutsCore.currentModifiers
    }

    fun toAmecs(): KeyModifiers {
        val keyModifiers = KeyModifiers()
        keyModifiers.shift = shift
        keyModifiers.control = ctrl
        keyModifiers.alt = alt
        return keyModifiers
    }
}