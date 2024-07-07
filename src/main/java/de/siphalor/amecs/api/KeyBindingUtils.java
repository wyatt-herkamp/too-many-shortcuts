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

package de.siphalor.amecs.api;


import java.util.Map;


import dev.kingtux.tms.mlayout.IKeyBinding;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;

/**
 * Utility methods and constants for Amecs and vanilla key bindings
 */
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class KeyBindingUtils {


    private static Map<String, KeyBinding> idToKeyBindingMap;

    private KeyBindingUtils() {
    }


    /**
     * Gets the key modifiers that are bound to the given key binding
     *
     * @param keyBinding the key binding
     * @return the key modifiers
     */
    public static KeyModifiers getBoundModifiers(KeyBinding keyBinding) {
        return ((IKeyBinding) keyBinding).tms$getKeyModifiers().toAmecs();
    }

    /**
     * Gets the default modifiers of the given key binding.
     * The returned value <b>must not be modified!</b>
     *
     * @param keyBinding the key binding
     * @return a reference to the default modifiers
     */
    public static KeyModifiers getDefaultModifiers(KeyBinding keyBinding) {
        if (keyBinding instanceof AmecsKeyBinding) {
            return ((AmecsKeyBinding) keyBinding).getDefaultModifiers();
        }
        return KeyModifiers.NO_MODIFIERS;
    }

    public static void resetBoundModifiers(KeyBinding keyBinding) {
        ((IKeyBinding) keyBinding).tms$getKeyModifiers().unset();
        if (keyBinding instanceof AmecsKeyBinding) {
            ((AmecsKeyBinding) keyBinding).resetKeyBinding();
        }
    }
}
