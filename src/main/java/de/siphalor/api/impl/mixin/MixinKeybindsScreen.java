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

package de.siphalor.api.impl.mixin;

import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.api.impl.duck.IKeyBinding;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.siphalor.amecs.api.KeyModifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@SuppressWarnings("WeakerAccess")
@Environment(EnvType.CLIENT)
@Mixin(KeybindsScreen.class)
public abstract class MixinKeybindsScreen extends GameOptionsScreen {
	@Shadow
	public KeyBinding selectedKeyBinding;

	@Shadow
	public long lastKeyCodeUpdateTime;

	@Shadow private ControlsListWidget controlsList;

	public MixinKeybindsScreen(Screen screen, GameOptions gameOptions, Text text) {
		super(screen, gameOptions, text);
	}

	@Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;setKeyCode(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/util/InputUtil$Key;)V"))
	public void onClicked(double x, double y, int type, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		InputUtil.Key key = ((IKeyBinding) selectedKeyBinding).amecs$getBoundKey();
		KeyModifiers keyModifiers = ((IKeyBinding) selectedKeyBinding).amecs$getKeyModifiers();
		if (!key.equals(InputUtil.UNKNOWN_KEY)) {
			keyModifiers.set(KeyModifier.fromKey(key), true);
		}
	}

	@Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;setKeyCode(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/util/InputUtil$Key;)V", ordinal = 0))
	public void clearKeyBinding(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		((IKeyBinding) selectedKeyBinding).amecs$getKeyModifiers().unset();
	}

	@Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;setKeyCode(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/util/InputUtil$Key;)V", ordinal = 1), cancellable = true)
	public void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (selectedKeyBinding.isUnbound()) {
			gameOptions.setKeyCode(selectedKeyBinding, InputUtil.fromKeyCode(keyCode, scanCode));
		} else {
			InputUtil.Key mainKey = ((IKeyBinding) selectedKeyBinding).amecs$getBoundKey();
			KeyModifiers keyModifiers = ((IKeyBinding) selectedKeyBinding).amecs$getKeyModifiers();
			KeyModifier mainKeyModifier = KeyModifier.fromKey(mainKey);
			KeyModifier keyModifier = KeyModifier.fromKeyCode(keyCode);
			if (mainKeyModifier != KeyModifier.NONE && keyModifier == KeyModifier.NONE) {
				keyModifiers.set(mainKeyModifier, true);
				gameOptions.setKeyCode(selectedKeyBinding, InputUtil.fromKeyCode(keyCode, scanCode));
				return;
			} else {
				keyModifiers.set(keyModifier, true);
				keyModifiers.cleanup(selectedKeyBinding);
			}
		}

		this.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs();
		this.controlsList.update();
		callbackInfoReturnable.setReturnValue(true);
	}
}
