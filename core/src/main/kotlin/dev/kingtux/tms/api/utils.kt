package dev.kingtux.tms.api

import dev.kingtux.tms.TooManyShortcutsCore
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.Options
import net.minecraft.client.KeyMapping
import net.minecraft.client.resources.language.I18n
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.network.chat.Component
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.Level

fun isDefaultBinding(keybinding: KeyMapping): Boolean {
    // If for some weird reason KeyMapping is not the Mixin type just use the default
    if (keybinding !is IKeyBinding) {
        keybinding.logInvalid()
        return false
    }
    // Anytime it has modifiers it is not the default
    if (keybinding.`tms$getKeyModifiers`().hasModifiers()) {
        return false
    }
    val boundKey = (keybinding as IKeyBinding).`tms$getBoundKey`()
    if (!keybinding.`tms$hasAlternatives`()) {
        return keybinding.defaultKey == boundKey
    }
    for (key in keybinding.`tms$getAlternatives`()!!) {
        if (!key.isDefault) {
            return false;
        }
    }
    return keybinding.defaultKey == boundKey
}

fun KeyMapping.isAlternative(): Boolean {
    if (this is IKeyBinding) {
        return this.`tms$hasAlternatives`()
    }
    return false
}

fun KeyMapping.hasConflicts(gameOptions: Options): Boolean {
    for (keyBinding in gameOptions.keyMappings) {
        if (keyBinding === this) {
            continue
        }
        if ((keyBinding as IKeyBinding).`tms$getBoundKey`().equals((this as IKeyBinding).`tms$getBoundKey`())) {
            return true
        }
    }
    return false
}

fun KeyMapping.entryKeyMatches(keyFilter: String?): Boolean {
    if (keyFilter == null) {
        return true
    }
    return when (keyFilter) {
        "" -> this.isUnbound
        else -> StringUtils.containsIgnoreCase(
            this.translatedKeyMessage.getString(),
            keyFilter
        )
    }
}

fun KeyMapping.translatedTextEqualsIgnoreCase(searchText: String): Boolean {

    return StringUtils.containsIgnoreCase(
        I18n.get(
            this.name
        ), searchText
    )
}

fun Component.equalsIgnoreCase(searchText: String): Boolean {
    return StringUtils.containsIgnoreCase(this.getString(), searchText)
}

fun KeyMapping.resetBinding(resetChildren: Boolean) {
    if (this !is IKeyBinding) {
        // THIS Should never happen. But if it does. Let's not crash the game.
        this.setKey(this.defaultKey)
        this.logInvalid()
        return
    }
    this.`tms$resetBinding`(resetChildren)
}

fun KeyMapping.clearBinding(resetChildren: Boolean) {
    if (this !is IKeyBinding) {
        // THIS Should never happen. But if it does. Let's not crash the game.
        this.setKey(InputConstants.UNKNOWN)
        this.logInvalid()
        return
    }
    this.`tms$clearBinding`(resetChildren)
}

fun KeyMapping.logInvalid() {
    TooManyShortcutsCore.LOGGER.log(
        Level.ERROR,
        "KeyMapping $this is not an IKeyBinding. This should never happen. Please report this to the mod author. Class Name: ${this.javaClass.name}"
    )
}

fun Options.hasModifiedKeyBindings(): Boolean {
    for (keyBinding in this.keyMappings) {
        if (!keyBinding.isDefault) {
            return true
        }
    }
    return false
}

fun isShiftKey(key: Int): Boolean {
    return key == 340 || key == 344
}