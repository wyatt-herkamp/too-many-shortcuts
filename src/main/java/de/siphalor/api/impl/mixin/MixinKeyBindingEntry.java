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

import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.api.impl.duck.IKeyBinding;
import de.siphalor.api.impl.duck.IKeyBindingEntry;
import dev.kingtux.tms.TooManyShortcuts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@Environment(EnvType.CLIENT)
@Mixin(ControlsListWidget.KeyBindingEntry.class)
public class MixinKeyBindingEntry implements IKeyBindingEntry {
	private static final String DESCRIPTION_SUFFIX = "." + TooManyShortcuts.MOD_ID + ".description";
	@Shadow
	@Final
	private KeyBinding binding;
	@Shadow
	@Final
	private ButtonWidget editButton;

	@Unique
	private List<Text> description;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onConstructed(ControlsListWidget parent, KeyBinding keyBinding, Text text, CallbackInfo callbackInfo) {
		String descriptionKey = binding.getTranslationKey() + DESCRIPTION_SUFFIX;
		if (I18n.hasTranslation(descriptionKey)) {
			String[] lines = StringUtils.split(I18n.translate(descriptionKey), '\n');
			description = new ArrayList<>(lines.length);
			for (String line : lines) {
				description.add(Text.literal(line));
			}
		} else {
			description = null;
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	public void onRendered(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta, CallbackInfo callbackInfo) {
		if (description != null && mouseY >= y && mouseY < y + entryHeight && mouseX < editButton.getX()) {
			context.drawTooltip(MinecraftClient.getInstance().textRenderer, description, mouseX, mouseY);
		}
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(
			method = "method_19870(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/gui/widget/ButtonWidget;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/option/ControlsListWidget;update()V")
	)
	public void onResetButtonClicked(KeyBinding keyBinding, ButtonWidget buttonWidget, CallbackInfo callbackInfo) {
		((IKeyBinding) binding).amecs$getKeyModifiers().unset();
		if (binding instanceof AmecsKeyBinding)
			((AmecsKeyBinding) binding).resetKeyBinding();
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "method_19871(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At("HEAD"))
	public void onEditButtonClicked(KeyBinding keyBinding, ButtonWidget buttonWidget, CallbackInfo callbackInfo) {
		((IKeyBinding) binding).amecs$getKeyModifiers().unset();
		binding.setBoundKey(InputUtil.UNKNOWN_KEY);
	}

	@Override
	public KeyBinding amecs$getKeyBinding() {
		return binding;
	}
}
