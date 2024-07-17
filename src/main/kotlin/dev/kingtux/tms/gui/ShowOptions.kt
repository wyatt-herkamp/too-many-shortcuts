package dev.kingtux.tms.gui

import dev.kingtux.tms.TooManyShortcuts
import dev.kingtux.tms.api.hasConflicts
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.resource.language.I18n
import net.minecraft.text.Text
import net.minecraft.util.TranslatableOption

enum class ShowOptions(val translationKeyExtension: String): TranslatableOption {
    SHOW_UNBOUND("unbound"),
    SHOW_CONFLICTS("conflicts"),
    SHOW_ALL("all");

    override fun getId(): Int {
        return ordinal
    }
    override fun getTranslationKey(): String {
        return fullTranslationKey();
    }
    fun fullTranslationKey(): String {
        return "${TooManyShortcuts.MOD_ID}.options.controls.show.${translationKeyExtension}";
    }

    fun doesKeyBindingMatchRequirements(keyBinding: KeyBinding, options: GameOptions): Boolean {
        if (keyBinding !is IKeyBinding) {
            return true;
        }
        return when (this) {
            SHOW_UNBOUND -> keyBinding.isUnbound
            SHOW_CONFLICTS -> {
                if (keyBinding.isUnbound) {
                    return false;
                }
                keyBinding.hasConflicts(options)
            }
            SHOW_ALL -> true
        }
    }
    fun next(): ShowOptions {
        return when (this) {
            SHOW_UNBOUND -> SHOW_CONFLICTS
            SHOW_CONFLICTS -> SHOW_ALL
            SHOW_ALL -> SHOW_UNBOUND
        }
    }
}
enum class ScreenModes(val title: String,val buttonTitle: String) {
    KeyBindings("controls.keybinds.title", "controls.keybinds"),
    FreeList("${TooManyShortcuts.MOD_ID}.controls.free_list.title", "${TooManyShortcuts.MOD_ID}.options.controls.free-list.button");
    fun next(): ScreenModes {
        return when (this) {
            KeyBindings -> FreeList
            FreeList -> KeyBindings
        }
    }
    fun buttonText(): Text{
        return Text.translatable("too_many_shortcuts.options.controls.screen-mode", I18n.translate(buttonTitle));
    }
}