package dev.kingtux.tms.gui.mixin;

import dev.kingtux.tms.compat.ClientCompat;
import dev.kingtux.tms.gui.TMSKeyBindsScreen;
import dev.kingtux.tms.mixin.helpers.MinecraftVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces the vanilla KeyBindsScreen with TMSKeyBindsScreen on Minecraft 26.2 and later,
 * where setScreen moved from Minecraft to {@link Gui}. See
 * {@link OpenTMSKeybindsScreenLegacy} for the 26.1 variant.
 * <p>
 * Intercepting here (rather than letting the screen open) ensures other mods that edit
 * the Controls Screen can still access it.
 */
@Environment(EnvType.CLIENT)
@MinecraftVersion(minecraftVersions = {">26.2"})
@Mixin(value = Gui.class, priority = 50)
public abstract class OpenTMSKeybindsScreenModern {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void openScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof KeyBindsScreen keybindsScreen) {
            ci.cancel();
            Minecraft mc = Minecraft.getInstance();
            ClientCompat.setScreen(mc, new TMSKeyBindsScreen(keybindsScreen.lastScreen, mc.options));
        }
    }
}
