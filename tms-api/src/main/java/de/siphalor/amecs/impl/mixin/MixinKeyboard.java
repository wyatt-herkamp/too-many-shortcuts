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

package de.siphalor.amecs.impl.mixin;

import de.siphalor.amecs.api.KeyModifier;
import de.siphalor.amecs.impl.AmecsAPI;
import de.siphalor.amecs.impl.KeyBindingManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Keyboard.class)
public class MixinKeyboard {

	@Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
	private void onKeyPriority(long window, int key, int scanCode, int action, int modifiers, CallbackInfo callbackInfo) {
		if (action == 1) {
			if (KeyBindingManager.onKeyPressedPriority(InputUtil.fromKeyCode(key, scanCode))) {
				callbackInfo.cancel();
			}
		} else if (action == 0) {
			if (KeyBindingManager.onKeyReleasedPriority(InputUtil.fromKeyCode(key, scanCode))) {
				callbackInfo.cancel();
			}
		}
	}

	@Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0))
	private void onKey(long window, int key, int scanCode, int action, int modifiers, CallbackInfo callbackInfo) {
		// Key released
		if (action == 0 && MinecraftClient.getInstance().currentScreen instanceof KeybindsScreen) {
			KeybindsScreen screen = (KeybindsScreen) MinecraftClient.getInstance().currentScreen;

			screen.selectedKeyBinding = null;
			screen.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs();
		}

		AmecsAPI.CURRENT_MODIFIERS.set(KeyModifier.fromKeyCode(InputUtil.fromKeyCode(key, scanCode).getCode()), action != 0);
	}
}
