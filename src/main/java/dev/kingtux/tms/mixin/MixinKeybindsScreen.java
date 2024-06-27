package dev.kingtux.tms.mixin;


import dev.kingtux.tms.mlayout.IKeyBinding;
import dev.kingtux.tms.api.modifiers.BindingModifiers;
import dev.kingtux.tms.api.modifiers.KeyModifier;
import dev.kingtux.tms.mlayout.IKeyBindsScreen;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
public abstract class MixinKeybindsScreen extends GameOptionsScreen  implements IKeyBindsScreen {
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
        InputUtil.Key key = ((IKeyBinding) selectedKeyBinding).tms$getBoundKey();
        BindingModifiers keyModifiers = ((IKeyBinding) selectedKeyBinding).tms$getKeyModifiers();
        if (!key.equals(InputUtil.UNKNOWN_KEY)) {
            keyModifiers.set(KeyModifier.Companion.fromKey(key), true);
        }
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;setKeyCode(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/util/InputUtil$Key;)V", ordinal = 0))
    public void clearKeyBinding(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        ((IKeyBinding) selectedKeyBinding).tms$getKeyModifiers().unset();
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;setKeyCode(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/util/InputUtil$Key;)V", ordinal = 1), cancellable = true)
    public void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (selectedKeyBinding.isUnbound()) {
            gameOptions.setKeyCode(selectedKeyBinding, InputUtil.fromKeyCode(keyCode, scanCode));
        } else {
            InputUtil.Key mainKey = ((IKeyBinding) selectedKeyBinding).tms$getBoundKey();
            BindingModifiers keyModifiers = ((IKeyBinding) selectedKeyBinding).tms$getKeyModifiers();
            KeyModifier mainKeyModifier = KeyModifier.Companion.fromKey(mainKey);
            KeyModifier keyModifier = KeyModifier.Companion.fromKeyCode(keyCode);
            if (mainKeyModifier != KeyModifier.NONE && keyModifier == KeyModifier.NONE) {
                keyModifiers.set(mainKeyModifier, true);
                gameOptions.setKeyCode(selectedKeyBinding, InputUtil.fromKeyCode(keyCode, scanCode));
                return;
            } else {
                keyModifiers.set(keyModifier, true);
                //TODO keyModifiers.cleanup(selectedKeyBinding);
            }
        }

        this.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs();
        this.controlsList.update();
        callbackInfoReturnable.setReturnValue(true);
    }

    @Override
    public ControlsListWidget tms$getControlsList() {
        return controlsList;
    }
}
