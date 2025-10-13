package dev.kingtux.tms.mixin;

import de.siphalor.amecs.KeyBindingManager;
import dev.kingtux.tms.TooManyShortcutsCore;
import dev.kingtux.tms.api.modifiers.KeyModifier;
import dev.kingtux.tms.gui.KeyBindingScreenType;
import dev.kingtux.tms.shortcuts.TmsShortcuts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Keyboard.class)
@Debug(export = true)
public class MixinKeyboard {
/*    @ModifyVariable(
            method = "onKey",
            argsOnly = true,
            ordinal = 0,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J")
    )
    public int modifyPressedKey(int key, long window, int key_, int scancode) {
        if (TmsShortcuts.INSTANCE.getEscapeKeyBinding().matchesKey(key, scancode)) {
            return GLFW.GLFW_KEY_ESCAPE;
        }
        return key;
    }*/

    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void onKeyPriority(long window, int action, KeyInput input, CallbackInfo ci) {
        if (action == 1) {
            if (KeyBindingManager.onKeyPressedPriority(InputUtil.fromKeyCode(input))) {
                ci.cancel();
            }
        } else if (action == 0) {
            if (KeyBindingManager.onKeyReleasedPriority(InputUtil.fromKeyCode(input))) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0))
    private void onKey(long window, int action, KeyInput input, CallbackInfo ci) {
        // Key released
        if (action == 0 && MinecraftClient.getInstance().currentScreen instanceof KeyBindingScreenType screen) {
            screen.setSelectedKeyBindingToNull();
            screen.setLastKeyCodeUpdateTime(Util.getMeasuringTimeMs());
        }
        KeyModifier keyModifier = KeyModifier.Companion.fromKeyCode(input.getKeycode());
        if (keyModifier != null) {
            TooManyShortcutsCore.INSTANCE.getCurrentModifiers().set(keyModifier, action != 0);
        }
    }
}
