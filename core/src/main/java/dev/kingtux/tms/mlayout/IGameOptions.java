package dev.kingtux.tms.mlayout;

import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

public interface IGameOptions {

    void removeKeyBinding(KeyMapping binding);

    void registerKeyBinding(KeyMapping binding);

    void registerKeyBindings(Options gameOptions, List<KeyMapping> bindings);
}
