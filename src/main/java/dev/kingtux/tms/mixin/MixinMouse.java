package dev.kingtux.tms.mixin;

import com.llamalad7.mixinextras.sugar.Local;
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
    private void beforeMouseScrollEvent( CallbackInfo ci,@Local(ordinal = 4) double vertical) {
        InputUtil.Key keyCode = ScrollKey.Companion.getVerticalKey(vertical).inputKey();
        //TooManyShortcuts.INSTANCE.log(Level.INFO, "Mouse Scroll Event: " + keyCode.toString() + " " + vertical);

        if (client.currentScreen != null){
            if (client.currentScreen instanceof KeybindsScreen){
                if (handleBindMouseScroll(((KeybindsScreen) client.currentScreen), keyCode)){
                    ci.cancel();
                }
            }
            return;
        }

        KeyBindingUtils.setLastScrollAmount(vertical);
        if (KeyBindingManager.onKeyPressedPriority(keyCode)) {
            ci.cancel();
        }
    }
    @Unique
    private boolean handleBindMouseScroll(@NotNull  KeybindsScreen screen, @NotNull InputUtil.Key keyCode){
        KeyBinding focusedBinding = screen.selectedKeyBinding;
        if (focusedBinding != null) {
            if (!focusedBinding.isUnbound()) {
                BindingModifiers keyModifiers = ((IKeyBinding) focusedBinding).tms$getKeyModifiers();
                keyModifiers.set(KeyModifier.Companion.fromKey(((IKeyBinding) focusedBinding).tms$getBoundKey()), true);
            }
            // This is a bit hacky, but the easiest way out
            // If the selected binding != null, the mouse x and y will always be ignored - so no need to convert them
            // The key code that InputUtil.MOUSE.createFromCode chooses is always one bigger than the input
            screen.mouseClicked(-1, -1, keyCode.getCode());
            return true;
        }
        return false;
    }

    @Override
    public boolean tms$getMouseScrolledEventUsed() {
        return mouseScrolled_eventUsed;
    }
}
