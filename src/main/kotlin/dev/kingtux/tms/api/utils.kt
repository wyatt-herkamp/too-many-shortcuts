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
    // If for some weird reason KeyBinding is not the Mixin type just use the default
    if (keybinding !is IKeyBinding) {
        return keybinding.defaultKey == keybinding.boundKey
    }
    // Anytime it has modifiers it is not the default
    if (keybinding.`tms$getKeyModifiers`().hasModifiers()){
        return false
    }
    if (!keybinding.`tms$hasAlternatives`()) {
        return keybinding.defaultKey == keybinding.boundKey
    }
    for (key in keybinding.`tms$getAlternatives`()!!) {
        if (!key.isDefault) {
            return false;
        }
    }
    return keybinding.defaultKey == keybinding.boundKey
}

fun KeyBinding.isAlternative(): Boolean {
    if (this is IKeyBinding) {
        return this.`tms$hasAlternatives`()
    }
    return false
}
fun KeyBinding.hasConflicts(gameOptions: GameOptions): Boolean {
    for (keyBinding in gameOptions.allKeys) {
        if (keyBinding === this){
            continue
        }
        if (keyBinding.boundKey.equals(this.boundKey)) {
            return true
        }
    }
    return false
}
fun KeyBinding.entryKeyMatches(keyFilter: String?): Boolean {
    if (keyFilter == null) {
        return true
    }
    return when (keyFilter) {
        "" -> this.isUnbound
        else -> StringUtils.containsIgnoreCase(
            this.boundKeyLocalizedText.string,
            keyFilter
        )
    }
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