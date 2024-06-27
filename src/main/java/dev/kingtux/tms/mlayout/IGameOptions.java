package dev.kingtux.tms.mlayout;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.List;

public interface IGameOptions {

    void removeKeyBinding(KeyBinding binding);

    void registerKeyBinding(KeyBinding binding);

    void registerKeyBindings(GameOptions gameOptions, List<KeyBinding> bindings);
}
