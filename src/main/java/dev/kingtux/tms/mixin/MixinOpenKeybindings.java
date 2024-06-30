package dev.kingtux.tms.mixin;

import dev.kingtux.tms.gui.TMSKeyBindsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.MouseOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ControlsOptionsScreen.class)
public abstract class MixinOpenKeybindings extends GameOptionsScreen {

    public MixinOpenKeybindings(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }


    /**
     * @author change the method
     * @reason Add the mouse settings and keybinds button to the options screen
     */
    @Inject(method = "addOptions", at = @At("HEAD"), cancellable = true)
    public void changeOptions(CallbackInfo ci) {
        this.body.addWidgetEntry(ButtonWidget.builder(Text.translatable("options.mouse_settings"), (buttonWidget) -> {
            this.client.setScreen(new MouseOptionsScreen(this, this.gameOptions));
        }).build(), ButtonWidget.builder(Text.translatable("controls.keybinds"), (buttonWidget) -> {
            this.client.setScreen(new TMSKeyBindsScreen(this, this.gameOptions));
        }).build());
        this.body.addAll(gameOptions.getSneakToggled(), gameOptions.getSprintToggled(), gameOptions.getAutoJump(), gameOptions.getOperatorItemsTab());
        ci.cancel();
    }
}
