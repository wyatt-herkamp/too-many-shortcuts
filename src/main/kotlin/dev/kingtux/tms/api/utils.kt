package dev.kingtux.tms.api

import dev.kingtux.tms.TooManyShortcuts
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.Level

fun isDefaultBinding(keybinding: KeyBinding): Boolean {
    if (keybinding is TMSKeyBinding) {
        // TODO: Handle TMSKeyBinding
        return false;
    }
    if (keybinding !is IKeyBinding) {
        return keybinding.defaultKey == keybinding.boundKey
    }
    if (!keybinding.`tms$hasAlternatives`()) {
        return keybinding.defaultKey == keybinding.boundKey
    }
    return false
}

fun KeyBinding.isAlternative(): Boolean {
    if (this is IKeyBinding) {
        return this.`tms$hasAlternatives`()
    }
    return false
}

fun KeyBinding.translatedTextEqualsIgnoreCase(searchText: String): Boolean {

    return StringUtils.containsIgnoreCase(
        I18n.translate(
            this.translationKey
        ), searchText
    )
}

fun Text.equalsIgnoreCase(searchText: String): Boolean {
    return StringUtils.containsIgnoreCase(this.string, searchText)
}

fun KeyBinding.resetBinding(resetChildren: Boolean) {
    if (this !is IKeyBinding) {
        // THIS Should never happen. But if it does. Let's not crash the game.
        this.setBoundKey(this.defaultKey)
        this.logInvalid()
        return
    }
    this.`tms$resetBinding`(resetChildren)
}

fun KeyBinding.clearBinding(resetChildren: Boolean) {
    if (this !is IKeyBinding) {
        // THIS Should never happen. But if it does. Let's not crash the game.
        this.setBoundKey(InputUtil.UNKNOWN_KEY)
        this.logInvalid()
        return
    }
    this.`tms$clearBinding`(resetChildren)
}

fun KeyBinding.logInvalid() {
    TooManyShortcuts.LOGGER.log(
        Level.ERROR,
        "KeyBinding $this is not an IKeyBinding. This should never happen. Please report this to the mod author. Class Name: ${this.javaClass.name}"
    )
}

fun GameOptions.hasModifiedKeyBindings(): Boolean {
    for (keyBinding in this.allKeys) {
        if (!keyBinding.isDefault) {
            return true
        }
    }
    return false
}

fun isShiftKey(key: Int): Boolean {
    return key == 340 || key == 344
}