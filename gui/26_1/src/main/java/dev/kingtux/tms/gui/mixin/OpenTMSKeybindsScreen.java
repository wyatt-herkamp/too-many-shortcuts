package dev.kingtux.tms.gui.mixin;

import dev.kingtux.tms.gui.TMSKeyBindsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = Minecraft.class, priority = 50)
public abstract class OpenTMSKeybindsScreen {

    /**
     * Intercepts the setScreen method to replace the KeybindsScreen with TMSKeyBindsScreen.
     * <p>
     * This is done to ensure that any other mods that edit the Controls Screen can still access it.
     *
     * @param screen the screen to open
     * @param ci     the callback info
     */
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void openScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof KeyBindsScreen) {
            ci.cancel();
            KeyBindsScreen keybindsScreen = (KeyBindsScreen) screen;
            Minecraft.getInstance().setScreen(new TMSKeyBindsScreen(keybindsScreen.lastScreen, Minecraft.getInstance().options));
        }
    }
}
