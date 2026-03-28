package dev.kingtux.tms.api

import de.siphalor.amecs.KeyBindingManager
import dev.kingtux.tms.TooManyShortcutsCore
import dev.kingtux.tms.api.modifiers.BindingModifiers
import dev.kingtux.tms.mlayout.IKeyBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.KeyMapping

@Environment(EnvType.CLIENT)
object TMSKeyBindingUtils {

    /**
     * Gets the "official" idToKeys map
     *
     * @return the map (use with care)
     */
    @JvmStatic
    fun getIdToKeyBindingMap(): Map<String, KeyMapping> {
        return KeyMapping.ALL
    }

    /**
     * Unregisters a keybinding from input querying but is NOT removed from the controls GUI
     * <br></br>
     * if you unregister a keybinding which is already in the controls GUI you can call [.registerHiddenKeyBinding] with this keybinding to undo this
     * <br></br>
     * <br></br>
     * This is possible even after the game initialized
     *
     * @param keyBinding the keybinding
     * @return whether the keyBinding was removed. It is not removed if it was not contained
     */
    @JvmStatic
    fun unregisterKeyBinding(keyBinding: KeyMapping): Boolean {
        return unregisterKeyBinding(keyBinding.name)
    }

    /**
     * Unregisters a keybinding with the given id
     * <br></br>
     * for more details [.unregisterKeyBinding]
     *
     * @param id the translation key
     * @return whether the keyBinding was removed. It is not removed if it was not contained
     * @see .unregisterKeyBinding
     */
    fun unregisterKeyBinding(id: String?): Boolean {
        val map = getIdToKeyBindingMap() as MutableMap
        val keyBinding = map.remove(id)
        return KeyBindingManager.unregister(keyBinding)
    }

    /**
     * Registers a keybinding for input querying but is NOT added to the controls GUI
     * <br></br>
     * you can register a keybinding which is already in the controls GUI but was removed from input querying via [.unregisterKeyBinding]
     * <br></br>
     * <br></br>
     * This is possible even after the game initialized
     *
     * @param keyBinding the keybinding
     * @return whether the keybinding was added. It is not added if it is already contained
     */
    fun registerHiddenKeyBinding(keyBinding: KeyMapping?): Boolean {
        return KeyBindingManager.register(keyBinding)
    }

    /**
     * Gets the key modifiers that are bound to the given key binding
     *
     * @param keyBinding the key binding
     * @return the key modifiers
     */
    @JvmStatic
    fun getBoundModifiers(keyBinding: KeyMapping): BindingModifiers? {
        return (keyBinding as IKeyBinding).`tms$getKeyModifiers`()
    }

    /**
     * Gets the key modifiers that are bound to the given key binding or an empty instance if none are bound
     *
     * @param keyBinding the key binding
     * @return the key modifiers
     */
    @JvmStatic
    fun getBoundModifiersOrEmpty(keyBinding: KeyMapping): BindingModifiers {
        return getBoundModifiers(keyBinding) ?: BindingModifiers()
    }

    /**
     * Gets the default modifiers of the given key binding.
     * The returned value **must not be modified!**
     *
     * @param keyBinding the key binding
     * @return a reference to the default modifiers
     */
    fun getDefaultModifiers(keyBinding: KeyMapping?): BindingModifiers? {
        if (keyBinding is TMSKeyBinding) {
            return keyBinding.defaultModifiers
        }
        return BindingModifiers()
    }

    fun resetBoundModifiers(keyBinding: KeyMapping) {
        (keyBinding as IKeyBinding).`tms$getKeyModifiers`().unset()
        if (keyBinding is TMSKeyBinding) {
            (keyBinding as TMSKeyBinding).resetKeyBinding()
        }
    }

    @JvmStatic
    fun debugKeyBinding(message: String, keyBinding: KeyMapping) {
        if (keyBinding is IKeyBinding) {
            TooManyShortcutsCore.LOGGER.debug("Debugging Key Binding {}, {}", message, keyBinding.`tms$debugString`())
        } else {
            TooManyShortcutsCore.LOGGER.debug(
                "Debugging Key Binding (Not TMS Keybinding) {}, {}",
                message,
                keyBinding.name
            )
        }
    }
}