package dev.kingtux.tms.api.modifiers

import de.siphalor.amecs.api.KeyModifiers
import kotlinx.serialization.Serializable


@Serializable
data class BindingModifiers(
    var shift: Boolean = false,
    var ctrl: Boolean = false,
    var alt: Boolean = false,
) {
    fun hasModifiers(): Boolean {
        return shift || ctrl || alt
    }

    fun isUnset(): Boolean {
        return !shift && !ctrl && !alt
    }

    fun unset() {
        shift = false
        ctrl = false
        alt = false
    }

    fun onlyOneSet(): Boolean {
        return (shift && !ctrl && !alt) || (!shift && ctrl && !alt) || (!shift && !ctrl && alt)
    }

    fun isSet(modifier: KeyModifier): Boolean {
        return when (modifier) {
            KeyModifier.SHIFT -> shift
            KeyModifier.CONTROL -> ctrl
            KeyModifier.ALT -> alt
            else -> false
        }
    }

    fun set(modifier: KeyModifier, value: Boolean) {
        when (modifier) {
            KeyModifier.SHIFT -> shift = value
            KeyModifier.CONTROL -> ctrl = value
            KeyModifier.ALT -> alt = value
        }
    }

    fun set(modifiers: de.siphalor.amecs.api.KeyModifiers) {
        shift = modifiers.shift
        ctrl = modifiers.control
        alt = modifiers.alt
    }

    fun set(modifiers: BindingModifiers) {
        shift = modifiers.shift
        ctrl = modifiers.ctrl
        alt = modifiers.alt
    }

    fun contains(boundModifiers: BindingModifiers): Boolean {

        return shift == boundModifiers.shift && ctrl == boundModifiers.ctrl && alt == boundModifiers.alt
    }

    fun toAmecs(): de.siphalor.amecs.api.KeyModifiers {
        val keyModifiers = KeyModifiers()
        keyModifiers.shift = shift
        keyModifiers.control = ctrl
        keyModifiers.alt = alt
        return keyModifiers
    }
}