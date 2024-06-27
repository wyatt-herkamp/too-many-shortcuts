package dev.kingtux.tms.mixin;

import de.siphalor.api.impl.duck.IMouse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

//@Environment(EnvType.CLIENT)
//@Debug(export = true)
//@Mixin(value = Mouse.class, priority = -2000)
public class MixinMouse implements IMouse {
    @Override
    public boolean amecs$getMouseScrolledEventUsed() {
        return false;
    }
}
