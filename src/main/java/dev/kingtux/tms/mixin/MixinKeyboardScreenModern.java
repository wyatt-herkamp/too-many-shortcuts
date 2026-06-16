package dev.kingtux.tms.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.KeyBindingManager;
import dev.kingtux.tms.mixin.helpers.MinecraftVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Priority key handler for Minecraft 26.2 and later, where {@code keyPress} reads the
 * current screen via {@code Minecraft.gui.screen()}; the anchor is therefore the
 * {@code Minecraft.gui} field access. Gated off below 26.2 by {@link MinecraftVersion};
 * see {@link MixinKeyboardScreenLegacy} for the 26.1 variant.
 */
@Environment(EnvType.CLIENT)
@MinecraftVersion(minecraftVersions = {">26.2"})
@Mixin(KeyboardHandler.class)
public class MixinKeyboardScreenModern {
    @Inject(method = "keyPress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;gui:Lnet/minecraft/client/gui/Gui;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void onKeyPriority(long window, int action, KeyEvent input, CallbackInfo ci) {
        if (action == 1) {
            if (KeyBindingManager.onKeyPressedPriority(InputConstants.getKey(input))) {
                ci.cancel();
            }
        } else if (action == 0) {
            if (KeyBindingManager.onKeyReleasedPriority(InputConstants.getKey(input))) {
                ci.cancel();
            }
        }
    }
}
