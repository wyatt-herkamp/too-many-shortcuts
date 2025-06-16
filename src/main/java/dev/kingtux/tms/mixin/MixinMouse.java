package dev.kingtux.tms.mixin;

import dev.kingtux.tms.shortcuts.TmsShortcuts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Mouse.class)
@Debug(export = true)
public class MixinMouse {
    @Inject(
            method = "onMouseButton",
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void modifyMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        // If the alternative keybinding is pressed. Then we need to cancel the event and call either the screen or the open menu
        if (TmsShortcuts.INSTANCE.getEscapeKeyBinding().matchesMouse(button) && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.currentScreen != null) {
                client.currentScreen.keyPressed(
                        GLFW.GLFW_KEY_ESCAPE,
                        button,
                        0
                );
            } else {
                client.openGameMenu(false);
            }
            ci.cancel();
        }
    }
}
