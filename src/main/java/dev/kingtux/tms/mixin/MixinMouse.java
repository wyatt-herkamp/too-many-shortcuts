package dev.kingtux.tms.mixin;

import dev.kingtux.tms.shortcuts.TmsShortcuts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
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
    public void modifyMouseButton(long window, MouseInput button, int action, CallbackInfo ci) {
        // If the alternative keybinding is pressed. Then we need to cancel the event and call either the screen or the open menu
        if (TmsShortcuts.INSTANCE.getEscapeKeyBinding().matchesMouse(new Click(
                0, 0, button
        )) && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.currentScreen != null) {
                client.currentScreen.keyPressed(
                    new KeyInput(                        GLFW.GLFW_KEY_ESCAPE,
                            button.button(),
                            0)
                );
            } else {
                client.openGameMenu(false);
            }
            ci.cancel();
        }
    }
}
