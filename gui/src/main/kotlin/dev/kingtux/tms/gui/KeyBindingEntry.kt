package dev.kingtux.tms.gui

import dev.kingtux.tms.api.modifiers.KeyModifier
import dev.kingtux.tms.api.modifiers.KeyModifier.Companion.fromKey
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.gui.Font
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.KeyMapping
import net.minecraft.client.resources.language.I18n
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.network.chat.Component
import org.apache.logging.log4j.Level

interface KeyBindingEntry<T : ControlsListWidget<*, *, *>> {
    var setModifierLast: Boolean
    val binding: KeyMapping
    val parent: T
    var duplicate: Boolean

    fun getWidth(
        renderer: Font
    ): Int

    fun updateMouseClick(button: Int) {
        if (binding !is IKeyBinding) {
            return
        }
        val iBinding = binding as IKeyBinding
        val keyCode = InputConstants.Type.MOUSE.getOrCreate(button)
        val key = (binding as IKeyBinding).`tms$getBoundKey`()
        val keyAsModifier = fromKey(key)
        if (key != InputConstants.UNKNOWN && keyAsModifier != null) {
            val keyModifiers = iBinding.`tms$getKeyModifiers`()
            keyModifiers.set(keyAsModifier, true)
        }
        binding.setKey(keyCode)

        TmsGUI.log(Level.INFO, "Mouse Click $button with ${iBinding.`tms$getKeyModifiers`()}")
        parent.parent.selectedKeyBinding = null

    }

    fun updateKeyboardInput(input: KeyEvent) {
        val newInput = InputConstants.getKey(input);
        if (binding.isUnbound) {
            binding.setKey(newInput)
        }
        if (binding !is IKeyBinding) {
            TmsGUI.log(Level.ERROR, "Binding is not a IKeyBinding")
            return;
        }
        val iBinding = binding as IKeyBinding

        // Gets the current bindings modifiers
        val keyModifiers = iBinding.`tms$getKeyModifiers`()
        // Get the active modifiers being pressed
        val activeModifiers = KeyModifier.fromModifiers(input.modifiers)
        TmsGUI.log(
            Level.INFO,
            "Key Code $input.key Scan Code ${input.scancode} Modifiers $activeModifiers from ${input.modifiers}"
        )

        // Remove all the modifiers then add the active ones
        keyModifiers.unset()
        if (activeModifiers.isNotEmpty()) {

            for (keyModifier in activeModifiers) {
                if (keyModifier.matches(input.key)) {
                    TmsGUI.log(Level.TRACE, "Ignoring Modifier $keyModifier due to matching key")
                    continue
                }
                TmsGUI.log(Level.TRACE, "Adding Modifier $keyModifier")
                keyModifiers.set(keyModifier, true)
            }
        }
        binding.setKey(newInput)
        TmsGUI.log(
            Level.INFO,
            "KeyBoard Click ${iBinding.`tms$getBoundKey`()} with ${iBinding.`tms$getKeyModifiers`()}"
        )
        if (!KeyModifier.Companion.isKeyModifier(newInput)) {
            parent.parent.selectedKeyBinding = null
            setModifierLast = false
        } else {
            // The task will wait a 500ms before clearing the selected key binding. This is allow the user to treat the selected key as modifier
            setModifierLast = true
            net.minecraft.util.Util.backgroundExecutor().execute {
                Thread.sleep(500)
                if (setModifierLast) {
                    parent.parent.selectedKeyBinding = null
                    setModifierLast = false
                    update()
                }
            }
        }
    }

    fun update()

    fun updateDuplicates(): Component? {
        this.duplicate = false
        val mutableText = Component.empty()
        if (!binding.isUnbound) {
            for (keyBinding in parent.parent.gameOptions().keyMappings) {
                if (keyBinding !== this.binding && binding.equals(keyBinding)) {
                    if (this.duplicate) {
                        mutableText.append(", ")
                    }
                    this.duplicate = true
                    val text = if (keyBinding is IKeyBinding && keyBinding.`tms$isAlternative`()) {
                        Component.translatable(
                            "too_many_shortcuts.options.controls.alternatives",
                            I18n.get(keyBinding.`tms$getParent`()!!.name),
                            keyBinding.`tms$getIndexInParent`()
                        )
                    } else {
                        Component.translatable(keyBinding.name)
                    }
                    mutableText.append(text)
                }
            }
        }
        return if (this.duplicate) {
            mutableText
        } else {
            null;
        }
    }

}