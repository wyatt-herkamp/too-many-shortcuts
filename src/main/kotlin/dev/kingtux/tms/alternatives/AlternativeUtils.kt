package dev.kingtux.tms.alternatives

import dev.kingtux.tms.mixin.EntryListWidgetAccessor
import dev.kingtux.tms.TooManyShortcuts.log
import dev.kingtux.tms.api.TMSKeyBinding
import dev.kingtux.tms.api.modifiers.BindingModifiers
import dev.kingtux.tms.mlayout.IKeyBinding
import dev.kingtux.tms.mlayout.IKeyBindsScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.option.ControlsListWidget
import net.minecraft.client.gui.screen.option.ControlsListWidget.KeyBindingEntry
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.apache.logging.log4j.Level
import java.lang.reflect.InvocationTargetException

fun resetSingleKeyBinding(keyBinding: KeyBinding) {
    keyBinding.setBoundKey(keyBinding.defaultKey)
    if (keyBinding is IKeyBinding) {
        (keyBinding as IKeyBinding).`tms$setKeyModifiers`(BindingModifiers())
    }
}

fun createAlternativeKeyBinding(base: KeyBinding): KeyBinding {
    return createAlternativeKeyBinding(base, -1)
}

fun createAlternativeKeyBinding(base: KeyBinding, code: Int): KeyBinding {
    return createAlternativeKeyBinding(base, InputUtil.Type.KEYSYM, code)
}

fun createAlternativeKeyBinding(base: KeyBinding, type: InputUtil.Type?, code: Int): KeyBinding {
    val parent = base as IKeyBinding
    val alt: KeyBinding = TMSKeyBinding(
        base,
        base.translationKey + "%" + parent.`tms$getNextChildId`(),
        type,
        code,
        base.category,
        BindingModifiers()
    )
    parent.`tms$addAlternative`(alt)
    return alt
}

fun getControlsListWidgetEntries(): List<KeyBindingEntry>? {
    val screen = MinecraftClient.getInstance().currentScreen
    if (screen is IKeyBindsScreen) {
        return ((screen as IKeyBindsScreen).`tms$getControlsList`() as EntryListWidgetAccessor).children as List<KeyBindingEntry>
    }
    return null
}

fun createKeyBindingEntry(listWidget: ControlsListWidget?, binding: KeyBinding, text: Text?): KeyBindingEntry? {
    try {
        // noinspection JavaReflectionMemberAccess,JavaReflectionMemberAccess
        val constructor = KeyBindingEntry::class.java.getDeclaredConstructor(
            ControlsListWidget::class.java,
            KeyBinding::class.java,
            Text::class.java
        )
        constructor.isAccessible = true
        return constructor.newInstance(listWidget, binding, text)
    } catch (e: IllegalAccessException) {
        log(Level.ERROR, "Failed to create keybinding entry: " + binding.translationKey + "Error: " + e.message)
    } catch (e: InstantiationException) {
        log(Level.ERROR, "Failed to create keybinding entry: " + binding.translationKey + "Error: " + e.message)
    } catch (e: InvocationTargetException) {
        log(Level.ERROR, "Failed to create keybinding entry: " + binding.translationKey + "Error: " + e.message)
    } catch (e: NoSuchMethodException) {
        log(Level.ERROR, "Failed to create keybinding entry: " + binding.translationKey + "Error: " + e.message)
    }
    return null
}