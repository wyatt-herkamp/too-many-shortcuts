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

package dev.kingtux.tms.mixin;

import dev.kingtux.tms.api.input.InputHandlerManager;
import dev.kingtux.tms.gui.TMSKeyBindsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
@Mixin(value = MinecraftClient.class, priority = 50)
public abstract class MixinMinecraftClient {

	@Inject(method = "handleInputEvents()V", at = @At(value = "HEAD"))
	private void handleInputEvents(CallbackInfo ci) {
		InputHandlerManager.handleInputEvents((MinecraftClient) (Object) this);
	}

	/**
	 * Intercepts the setScreen method to replace the KeybindsScreen with TMSKeyBindsScreen.
	 *
	 * This is done to ensure that any other mods that edit the Controls Screen can still access it.
	 * @param screen the screen to open
	 * @param ci the callback info
	 */
	@Inject(method = "setScreen", at = @At("HEAD"),cancellable = true)
	private void openScreen(Screen screen, CallbackInfo ci) {
		if (screen instanceof KeybindsScreen){
			ci.cancel();
			KeybindsScreen keybindsScreen = (KeybindsScreen) screen;
			MinecraftClient.getInstance().setScreen(new TMSKeyBindsScreen(keybindsScreen.parent, MinecraftClient.getInstance().options));
		}
	}
}
