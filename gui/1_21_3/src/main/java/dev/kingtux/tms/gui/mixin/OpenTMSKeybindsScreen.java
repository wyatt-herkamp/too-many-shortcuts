package dev.kingtux.tms.gui.mixin;

import dev.kingtux.tms.gui.TMSKeyBindsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = MinecraftClient.class, priority = 50)
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
        if (screen instanceof KeybindsScreen) {
            ci.cancel();
            KeybindsScreen keybindsScreen = (KeybindsScreen) screen;
            MinecraftClient.getInstance().setScreen(new TMSKeyBindsScreen(keybindsScreen.parent, MinecraftClient.getInstance().options));
        }
    }
}
