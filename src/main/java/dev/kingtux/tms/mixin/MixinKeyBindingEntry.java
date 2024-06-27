package dev.kingtux.tms.mixin;

import com.google.common.collect.ImmutableList;
import dev.kingtux.tms.alternatives.AlternativeUtilsKt;
import dev.kingtux.tms.api.TMSKeyBinding;
import dev.kingtux.tms.mlayout.IGameOptions;
import dev.kingtux.tms.mlayout.IKeyBinding;
import dev.kingtux.tms.mlayout.IKeyBindingEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static dev.kingtux.tms.gui.UtilsKt.*;

@SuppressWarnings("WeakerAccess")
@Environment(EnvType.CLIENT)
@Mixin(ControlsListWidget.KeyBindingEntry.class)
public class MixinKeyBindingEntry implements IKeyBindingEntry {
    @Shadow
    @Final
    private KeyBinding binding;
    @Shadow
    @Final
    private ButtonWidget editButton;

    @Unique
    private List<Text> description;

    @Shadow
    @Final
    private ButtonWidget resetButton;

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
        IKeyBinding iKeyBinding = (IKeyBinding) binding;
        if (iKeyBinding.tms$isAlternative()) {
            bindingName = entryName();
            alternativesButton = ButtonWidget.builder(Text.literal("x"), button -> {
                ((IKeyBinding) iKeyBinding.tms$getParent()).tms$removeAlternative(binding);
                ((IGameOptions) MinecraftClient.getInstance().options).removeKeyBinding(binding);
                List<ControlsListWidget.KeyBindingEntry> entries = AlternativeUtilsKt.getControlsListWidgetEntries();
                if (entries != null) {
                    entries.remove((ControlsListWidget.KeyBindingEntry) (Object) this);
                }
            }).size(20, 20).build();
        } else {
            alternativesButton = ButtonWidget.builder(Text.literal("+"), button -> {
                KeyBinding altBinding = AlternativeUtilsKt.createAlternativeKeyBinding(binding);
                ((IGameOptions) MinecraftClient.getInstance().options).registerKeyBinding(altBinding);
                ControlsListWidget.KeyBindingEntry altEntry = AlternativeUtilsKt.createKeyBindingEntry(parent, altBinding, Text.literal("..."));
                if (altEntry != null) {
                    List<ControlsListWidget.KeyBindingEntry> entries = AlternativeUtilsKt.getControlsListWidgetEntries();
                    if (entries != null) {
                        for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
                            //noinspection ConstantConditions,RedundantCast,RedundantCast
                            if (entries.get(i) == (ControlsListWidget.KeyBindingEntry) (Object) this) {
                                i += ((IKeyBinding) binding).tms$getAlternativesCount();
                                entries.add(i, altEntry);
                                break;
                            }
                        }
                    }
                }
            }).size(20, 20).build();
            resetButton.setTooltip(Tooltip.of(resetTooltip()));
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void onRendered(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta, CallbackInfo callbackInfo) {
        if (description != null && mouseY >= y && mouseY < y + entryHeight && mouseX < editButton.getX()) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, description, mouseX, mouseY);
        }
        alternativesButton.setY(resetButton.getY());
        alternativesButton.setX(resetButton.getX() + resetButton.getWidth() + 10);
        alternativesButton.render(context, mouseX, mouseY, delta);
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "method_19870(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/gui/widget/ButtonWidget;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/option/ControlsListWidget;update()V")
    )
    public void onResetButtonClicked(KeyBinding keyBinding, ButtonWidget buttonWidget, CallbackInfo callbackInfo) {
        ((IKeyBinding) binding).tms$getKeyModifiers().unset();
        if (binding instanceof TMSKeyBinding)
            ((TMSKeyBinding) binding).resetKeyBinding();
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "method_19871(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At("HEAD"))
    public void onEditButtonClicked(KeyBinding keyBinding, ButtonWidget buttonWidget, CallbackInfo callbackInfo) {
        ((IKeyBinding) binding).tms$getKeyModifiers().unset();
        binding.setBoundKey(InputUtil.UNKNOWN_KEY);
    }

    @ModifyArg(method="render", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;setPosition(II)V"), index=0)
    private int adjustXPosition(int original) {
        return original - 30;
    }

    @Inject(method = "children", at = @At("RETURN"), cancellable = true)
    public void children(CallbackInfoReturnable<List<? extends Element>> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(ImmutableList.of(editButton, resetButton, alternativesButton));
    }

    @Inject(method = "selectableChildren", at = @At("RETURN"), cancellable = true)
    public void selectableChildren(CallbackInfoReturnable<List<? extends Element>> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(ImmutableList.of(editButton, resetButton, alternativesButton));
    }
    @Override
    public KeyBinding tms$getKeyBinding() {
        return binding;
    }

    @Override
    public ButtonWidget tms$getEditButton() {
        return editButton;
    }
}
