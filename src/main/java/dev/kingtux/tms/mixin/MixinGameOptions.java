package dev.kingtux.tms.mixin;


import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.KeyBindingManager;
import dev.kingtux.tms.config.ConfigManager;
import dev.kingtux.tms.mlayout.IGameOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
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
@Mixin(Options.class)
public class MixinGameOptions implements IGameOptions {
    @Shadow
    @Final
    @Mutable
    public KeyMapping[] keyMappings;


    @Inject(method = "save", at = @At("RETURN"))
    public void write(CallbackInfo callbackInfo) {
        ConfigManager config = ConfigManager.Companion.instance();
        config.saveBindings(keyMappings);
    }

    @Inject(method = "load", at = @At("RETURN"))
    public void load(CallbackInfo callbackInfo) {
        ConfigManager configManager = ConfigManager.Companion.instance();
        keyMappings = configManager.loadBindings(keyMappings);
        KeyMapping.resetMapping();
    }

    @Override
    public void removeKeyBinding(KeyMapping binding) {
        binding.setKey(InputConstants.UNKNOWN);
        KeyBindingManager.unregister(binding);
        KeyMapping[] keysAll = keyMappings;
        int index = ArrayUtils.indexOf(keysAll, binding);
        KeyMapping[] newKeysAll = new KeyMapping[keysAll.length - 1];
        System.arraycopy(keysAll, 0, newKeysAll, 0, index);
        System.arraycopy(keysAll, index + 1, newKeysAll, index, keysAll.length - index - 1);
        keyMappings = newKeysAll;
        KeyMapping.resetMapping();
    }

    @Override
    public void registerKeyBinding(KeyMapping binding) {
        KeyMapping[] keysAll = keyMappings;
        KeyMapping[] newKeysAll = new KeyMapping[keysAll.length + 1];
        System.arraycopy(keysAll, 0, newKeysAll, 0, keysAll.length);
        newKeysAll[keysAll.length] = binding;
        keyMappings = newKeysAll;

        KeyMapping.resetMapping();
    }

    @Override
    public void registerKeyBindings(Options gameOptions, List<KeyMapping> bindings) {
        KeyMapping[] keysAll = keyMappings;
        KeyMapping[] newKeysAll = new KeyMapping[keysAll.length + bindings.size()];
        System.arraycopy(keysAll, 0, newKeysAll, 0, keysAll.length);
        int i = keysAll.length;
        for (KeyMapping binding : bindings) {
            newKeysAll[i] = binding;
            i++;
        }
        keyMappings = newKeysAll;
        KeyMapping.resetMapping();
    }
}
