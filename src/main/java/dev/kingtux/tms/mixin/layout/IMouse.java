package dev.kingtux.tms.mixin.layout;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IMouse {
    // this is used by KTIG
    boolean tms$getMouseScrolledEventUsed();
}
