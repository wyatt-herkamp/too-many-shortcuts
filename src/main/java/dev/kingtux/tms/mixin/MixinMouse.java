package dev.kingtux.tms.mixin;

import dev.kingtux.tms.shortcuts.TmsShortcuts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MouseHandler.class)
@Debug(export = true)
public class MixinMouse {
    @Inject(
            method = "onButton",
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void modifyMouseButton(long window, MouseButtonInfo button, int action, CallbackInfo ci) {
        // If the alternative keybinding is pressed. Then we need to cancel the event and call either the screen or the open menu
        if (TmsShortcuts.INSTANCE.getEscapeKeyBinding().matchesMouse(new MouseButtonEvent(
                0, 0, button
        )) && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            Minecraft client = Minecraft.getInstance();
            if (client.screen != null) {
                client.screen.keyPressed(
                    new KeyEvent(                        GLFW.GLFW_KEY_ESCAPE,
                            button.button(),
                            0)
                );
            } else {
                client.pauseGame(false);
            }
            ci.cancel();
        }
    }
}
