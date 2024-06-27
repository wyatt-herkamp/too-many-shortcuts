/*
 * Copyright 2020-2023 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.kingtux.tms.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import de.siphalor.amecs.KeyBindingManager;

import dev.kingtux.tms.mlayout.IKeyBinding;
import dev.kingtux.tms.api.modifiers.BindingModifiers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;

/**
 * Utility methods and constants for Amecs and vanilla key bindings
 */
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class KeyBindingUtils {
	/**
	 * The last (y directional) scroll delta
	 */
	private static double lastScrollAmount = 0;
	private static Map<String, KeyBinding> idToKeyBindingMap;

	private KeyBindingUtils() {}
	public static double getLastScrollAmount() {
		return lastScrollAmount;
	}
	/**
	 * Sets the last (y directional) scroll amount. <b>For internal use only.</b>
	 *
	 * @param lastScrollAmount the amount
	 */
	public static void setLastScrollAmount(double lastScrollAmount) {
		KeyBindingUtils.lastScrollAmount = lastScrollAmount;
	}


	/**
	 * Gets the "official" idToKeys map
	 *
	 * @return the map (use with care)
	 */
	public static Map<String, KeyBinding> getIdToKeyBindingMap() {
		if (idToKeyBindingMap == null) {
			try {
				// reflections accessors should be initialized statically if they are static
				// but in this case its fine because we only do this once because it is cached in a static field

				// noinspection JavaReflectionMemberAccess
				Method method = KeyBinding.class.getDeclaredMethod("tms$getIdToKeyBindingMap");
				method.setAccessible(true);
				// noinspection unchecked
				idToKeyBindingMap = (Map<String, KeyBinding>) method.invoke(null);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				//log.error("Failed to get access to key bindings", e);
			}
		}
		return idToKeyBindingMap;
	}

	/**
	 * Unregisters a keybinding from input querying but is NOT removed from the controls GUI
	 * <br>
	 * if you unregister a keybinding which is already in the controls GUI you can call {@link #registerHiddenKeyBinding(KeyBinding)} with this keybinding to undo this
	 * <br>
	 * <br>
	 * This is possible even after the game initialized
	 *
	 * @param keyBinding the keybinding
	 * @return whether the keyBinding was removed. It is not removed if it was not contained
	 */
	public static boolean unregisterKeyBinding(KeyBinding keyBinding) {
		return unregisterKeyBinding(keyBinding.getTranslationKey());
	}

	/**
	 * Unregisters a keybinding with the given id
	 * <br>
	 * for more details {@link #unregisterKeyBinding(KeyBinding)}
	 *
	 * @see #unregisterKeyBinding(KeyBinding)
	 * @param id the translation key
	 * @return whether the keyBinding was removed. It is not removed if it was not contained
	 */
	public static boolean unregisterKeyBinding(String id) {
		KeyBinding keyBinding = getIdToKeyBindingMap().remove(id);
		return KeyBindingManager.unregister(keyBinding);
	}

	/**
	 * Registers a keybinding for input querying but is NOT added to the controls GUI
	 * <br>
	 * you can register a keybinding which is already in the controls GUI but was removed from input querying via {@link #unregisterKeyBinding(KeyBinding)}
	 * <br>
	 * <br>
	 * This is possible even after the game initialized
	 *
	 * @param keyBinding the keybinding
	 * @return whether the keybinding was added. It is not added if it is already contained
	 */
	public static boolean registerHiddenKeyBinding(KeyBinding keyBinding) {
		return KeyBindingManager.register(keyBinding);
	}

	/**
	 * Gets the key modifiers that are bound to the given key binding
	 * @param keyBinding the key binding
	 * @return the key modifiers
	 */
	public static BindingModifiers getBoundModifiers(KeyBinding keyBinding) {
		return ((IKeyBinding) keyBinding).tms$getKeyModifiers();
	}

	/**
	 * Gets the default modifiers of the given key binding.
	 * The returned value <b>must not be modified!</b>
	 * @param keyBinding the key binding
	 * @return a reference to the default modifiers
	 */
	public static BindingModifiers getDefaultModifiers(KeyBinding keyBinding) {
		if (keyBinding instanceof TMSKeyBinding) {
			return ((TMSKeyBinding) keyBinding).getDefaultModifiers();
		}
		return new BindingModifiers();
	}

	public static void resetBoundModifiers(KeyBinding keyBinding) {
		((IKeyBinding) keyBinding).tms$getKeyModifiers().unset();
		if (keyBinding instanceof TMSKeyBinding) {
			((TMSKeyBinding) keyBinding).resetKeyBinding();
		}
	}
}
