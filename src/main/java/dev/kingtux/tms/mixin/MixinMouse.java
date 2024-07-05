package dev.kingtux.tms.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.kingtux.tms.TooManyShortcuts;
import dev.kingtux.tms.api.KeyBindingUtils;
import de.siphalor.amecs.KeyBindingManager;
import dev.kingtux.tms.api.scroll.ScrollKey;
import dev.kingtux.tms.mlayout.IKeyBinding;
import dev.kingtux.tms.mlayout.IMouse;
import dev.kingtux.tms.api.modifiers.BindingModifiers;
import dev.kingtux.tms.api.modifiers.KeyModifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Debug(export = true)
@Mixin(value = Mouse.class, priority = -2000)
public class MixinMouse implements IMouse {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private double eventDeltaVerticalWheel;

    @Unique
    private boolean mouseScrolled_eventUsed;


    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDDD)Z"), cancellable = true)
    private void beforeMouseScrollEvent(CallbackInfo ci, @Local(ordinal = 3) double horizontal, @Local(ordinal = 4) double vertical) {
        ScrollKey scrollKey = ScrollKey.Companion.getScrollKey(vertical, horizontal);

        if (scrollKey == null) {
            return;
        }

        InputUtil.Key keyCode = scrollKey.inputKey();
        if (client.currentScreen != null) {
            return;
        }
        TooManyShortcuts.INSTANCE.getLOGGER().info("Mouse Scroll Event: {} {} {}", scrollKey, vertical, horizontal);

        KeyBindingUtils.setLastScrollAmount(vertical);
        if (KeyBindingManager.onKeyPressedPriority(keyCode)) {

            ci.cancel();
        }
    }
    
    @Override
    public boolean tms$getMouseScrolledEventUsed() {
        return mouseScrolled_eventUsed;
    }
}
