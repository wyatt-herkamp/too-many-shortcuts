package dev.kingtux.tms.mixin;


import de.siphalor.amecs.KeyBindingManager;
import dev.kingtux.tms.config.ConfigManager;
import dev.kingtux.tms.mlayout.IGameOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(GameOptions.class)
public class MixinGameOptions implements IGameOptions {
    @Shadow
    @Final
    @Mutable
    public KeyBinding[] allKeys;


    @Inject(method = "write", at = @At("RETURN"))
    public void write(CallbackInfo callbackInfo) {
        ConfigManager config = ConfigManager.Companion.instance();
        config.saveBindings(allKeys);
    }

    @Inject(method = "load", at = @At("RETURN"))
    public void load(CallbackInfo callbackInfo) {
        ConfigManager configManager = ConfigManager.Companion.instance();
        allKeys = configManager.loadBindings(allKeys);
        KeyBinding.updateKeysByCode();
    }

    @Override
    public void removeKeyBinding(KeyBinding binding) {
        binding.setBoundKey(InputUtil.UNKNOWN_KEY);
        KeyBindingManager.unregister(binding);
        KeyBinding[] keysAll = allKeys;
        int index = ArrayUtils.indexOf(keysAll, binding);
        KeyBinding[] newKeysAll = new KeyBinding[keysAll.length - 1];
        System.arraycopy(keysAll, 0, newKeysAll, 0, index);
        System.arraycopy(keysAll, index + 1, newKeysAll, index, keysAll.length - index - 1);
        allKeys = newKeysAll;
        KeyBinding.updateKeysByCode();
    }

    @Override
    public void registerKeyBinding(KeyBinding binding) {
        KeyBinding[] keysAll = allKeys;
        KeyBinding[] newKeysAll = new KeyBinding[keysAll.length + 1];
        System.arraycopy(keysAll, 0, newKeysAll, 0, keysAll.length);
        newKeysAll[keysAll.length] = binding;
        allKeys = newKeysAll;

        KeyBinding.updateKeysByCode();
    }

    @Override
    public void registerKeyBindings(GameOptions gameOptions, List<KeyBinding> bindings) {
        KeyBinding[] keysAll = allKeys;
        KeyBinding[] newKeysAll = new KeyBinding[keysAll.length + bindings.size()];
        System.arraycopy(keysAll, 0, newKeysAll, 0, keysAll.length);
        int i = keysAll.length;
        for (KeyBinding binding : bindings) {
            newKeysAll[i] = binding;
            i++;
        }
        allKeys = newKeysAll;
        KeyBinding.updateKeysByCode();
    }
}
