/*
 * Copyright 2021 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.siphalor.amecs.mixin;

import com.google.common.collect.ImmutableList;
import de.siphalor.nmuk.impl.IKeyBinding;
import de.siphalor.nmuk.impl.NMUKKeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public abstract class MixinKeyBindingEntry {
	private static final Text ENTRY_NAME = Text.literal("    ->");
	private static final Text RESET_TOOLTIP = Text.translatable("too_many_shortcuts.options.controls.reset.tooltip");

	@Shadow
	@Final
	private ButtonWidget resetButton;
	@Shadow
	@Final
	private ButtonWidget editButton;
	@Mutable
	@Shadow
	@Final
	private Text bindingName;
	// This is a synthetic field containing the outer class instance
	@Shadow(aliases = "field_2742", remap = false)
	@Final
	private ControlsListWidget listWidget;

	@Unique
	private ButtonWidget alternativesButton;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onConstruct(ControlsListWidget outer, KeyBinding binding, Text text, CallbackInfo ci) {
		IKeyBinding iKeyBinding = (IKeyBinding) binding;
		if (iKeyBinding.nmuk_isAlternative()) {
			bindingName = ENTRY_NAME;
			alternativesButton = ButtonWidget.builder(Text.literal("x"), button -> {
				((IKeyBinding) iKeyBinding.nmuk_getParent()).nmuk_removeAlternative(binding);
				NMUKKeyBindingHelper.removeKeyBinding(binding);
				List<ControlsListWidget.KeyBindingEntry> entries = NMUKKeyBindingHelper.getControlsListWidgetEntries();
				if (entries != null) {
					entries.remove((ControlsListWidget.KeyBindingEntry) (Object) this);
				}
			}).size(20, 20).build();
		} else {
			alternativesButton = ButtonWidget.builder(Text.literal("+"), button -> {
				KeyBinding altBinding = NMUKKeyBindingHelper.createAlternativeKeyBinding(binding);
				NMUKKeyBindingHelper.registerKeyBinding(altBinding);
				ControlsListWidget.KeyBindingEntry altEntry = NMUKKeyBindingHelper.createKeyBindingEntry(outer, altBinding, Text.literal("..."));
				if (altEntry != null) {
					List<ControlsListWidget.KeyBindingEntry> entries = NMUKKeyBindingHelper.getControlsListWidgetEntries();
					if (entries != null) {
						for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
							//noinspection ConstantConditions,RedundantCast,RedundantCast
							if (entries.get(i) == (ControlsListWidget.KeyBindingEntry) (Object) this) {
								i += ((IKeyBinding) binding).nmuk_getAlternativesCount();
								entries.add(i, altEntry);
								break;
							}
						}
					}
				}
			}).size(20, 20).build();
			resetButton.setTooltip(Tooltip.of(RESET_TOOLTIP));
		}
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "method_19870(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At("HEAD"))
	private void resetButtonPressed(KeyBinding keyBinding, ButtonWidget widget, CallbackInfo ci) {
		if (((IKeyBinding) keyBinding).nmuk_getParent() == null && Screen.hasShiftDown()) {
			List<KeyBinding> alternatives = ((IKeyBinding) keyBinding).nmuk_getAlternatives();
			List<KeyBinding> defaultAlternatives = new ArrayList<>(NMUKKeyBindingHelper.defaultAlternatives.get(keyBinding));
			List<ControlsListWidget.KeyBindingEntry> entries = NMUKKeyBindingHelper.getControlsListWidgetEntries();
			// noinspection ConstantConditions
			int entryPos = entries.indexOf((ControlsListWidget.KeyBindingEntry) (Object) this);

			int index;
			for (Iterator<KeyBinding> iterator = alternatives.iterator(); iterator.hasNext(); ) {
				KeyBinding alternative = iterator.next();
				index = defaultAlternatives.indexOf(alternative);
				if (index == -1) {
					entries.remove(entryPos + 1 + ((IKeyBinding) alternative).nmuk_getIndexInParent());
					iterator.remove();
					NMUKKeyBindingHelper.removeKeyBinding(alternative);
					continue;
				}
				defaultAlternatives.remove(index);
				NMUKKeyBindingHelper.resetSingleKeyBinding(alternative);
			}
			entryPos += alternatives.size();

			ControlsListWidget.KeyBindingEntry entry;
			NMUKKeyBindingHelper.registerKeyBindings(MinecraftClient.getInstance().options, defaultAlternatives);
			for (KeyBinding defaultAlternative : defaultAlternatives) {
				entry = NMUKKeyBindingHelper.createKeyBindingEntry(listWidget, defaultAlternative, ENTRY_NAME);
				entries.add(++entryPos, entry);
				NMUKKeyBindingHelper.resetSingleKeyBinding(defaultAlternative);
			}
		}
	}

	@ModifyArg(method="render", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;setPosition(II)V"), index=0)
	private int adjustXPosition(int original) {
		return original - 30;
	}

	@Inject(method = "render", at = @At("RETURN"))
	public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo callbackInfo) {
		alternativesButton.setY(resetButton.getY());
		alternativesButton.setX(resetButton.getX() + resetButton.getWidth() + 10);
		alternativesButton.render(drawContext, mouseX, mouseY, tickDelta);
	}

	@Inject(method = "children", at = @At("RETURN"), cancellable = true)
	public void children(CallbackInfoReturnable<List<? extends Element>> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(ImmutableList.of(editButton, resetButton, alternativesButton));
	}

    @Inject(method = "selectableChildren", at = @At("RETURN"), cancellable = true)
    public void selectableChildren(CallbackInfoReturnable<List<? extends Element>> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(ImmutableList.of(editButton, resetButton, alternativesButton));
    }
}
