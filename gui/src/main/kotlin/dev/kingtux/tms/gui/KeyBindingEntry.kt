package dev.kingtux.tms.gui

import dev.kingtux.tms.api.modifiers.KeyModifier
import dev.kingtux.tms.api.modifiers.KeyModifier.Companion.fromKey
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.Util
import org.apache.logging.log4j.Level

interface KeyBindingEntry<T : ControlsListWidget<*, *, *>> {
    var setModifierLast: Boolean
    val binding: KeyBinding
    val parent: T
    var duplicate: Boolean

    fun getWidth(
        renderer: TextRenderer
    ): Int

    fun updateMouseClick(button: Int) {
        if (binding !is IKeyBinding) {
            return
        }
        val iBinding = binding as IKeyBinding
        val keyCode = InputUtil.Type.MOUSE.createFromCode(button)
        val key = (binding as IKeyBinding).`tms$getBoundKey`()
        val keyAsModifier = fromKey(key)
        if (key != InputUtil.UNKNOWN_KEY && keyAsModifier != null) {
            val keyModifiers = iBinding.`tms$getKeyModifiers`()
            keyModifiers.set(keyAsModifier, true)
        }
        binding.setBoundKey(keyCode)

        TmsGUI.log(Level.INFO, "Mouse Click $button with ${iBinding.`tms$getKeyModifiers`()}")
        parent.parent.selectedKeyBinding = null

    }

    fun updateKeyboardInput(keyCode: Int, scanCode: Int, modifiers: Int) {
        val newInput = InputUtil.fromKeyCode(keyCode, scanCode);
        if (binding.isUnbound) {
            binding.setBoundKey(newInput)
        }
        if (binding !is IKeyBinding) {
            TmsGUI.log(Level.ERROR, "Binding is not a IKeyBinding")
            return;
        }
        val iBinding = binding as IKeyBinding

        // Gets the current bindings modifiers
        val keyModifiers = iBinding.`tms$getKeyModifiers`()
        // Get the active modifiers being pressed
        val activeModifiers = KeyModifier.fromModifiers(modifiers)
        TmsGUI.log(
            Level.INFO,
            "Key Code $keyCode Scan Code $scanCode Modifiers $activeModifiers from $modifiers"
        )

        // Remove all the modifiers then add the active ones
        keyModifiers.unset()
        if (activeModifiers.isNotEmpty()) {

            for (keyModifier in activeModifiers) {
                if (keyModifier.matches(keyCode)) {
                    TmsGUI.log(Level.TRACE, "Ignoring Modifier $keyModifier due to matching key")
                    continue
                }
                TmsGUI.log(Level.TRACE, "Adding Modifier $keyModifier")
                keyModifiers.set(keyModifier, true)
            }
        }
        binding.setBoundKey(newInput)
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
            Util.getMainWorkerExecutor().execute {
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

    fun updateDuplicates(): Text? {
        this.duplicate = false
        val mutableText = Text.empty()
        if (!binding.isUnbound) {
            for (keyBinding in parent.parent.gameOptions().allKeys) {
                if (keyBinding !== this.binding && binding.equals(keyBinding)) {
                    if (this.duplicate) {
                        mutableText.append(", ")
                    }
                    this.duplicate = true
                    val text = if (keyBinding is IKeyBinding && keyBinding.`tms$isAlternative`()) {
                        Text.translatable(
                            "too_many_shortcuts.options.controls.alternatives",
                            I18n.translate(keyBinding.`tms$getParent`()!!.translationKey),
                            keyBinding.`tms$getIndexInParent`()
                        )
                    } else {
                        Text.translatable(keyBinding.translationKey)
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