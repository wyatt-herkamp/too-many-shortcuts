package dev.kingtux.tms.gui.mixin;

import dev.kingtux.tms.gui.mlayout.IScrollMixin;
import dev.kingtux.tms.mixin.helpers.MinecraftVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
@MinecraftVersion(
        minecraftVersions = {"1.21.3"})
@Environment(EnvType.CLIENT)
@Mixin(EntryListWidget.class)
public abstract class Mixin1213EntryListWidget implements IScrollMixin {
    @Shadow
    public abstract void setScrollAmountOnly(double scrollY);
    @Shadow
    public abstract double getScrollAmount();

    @Shadow
    protected abstract int getScrollbarX();

    public void tms$setScrollY(double scrollY) {
        setScrollAmountOnly(scrollY);
    }

    public double tms$getScrollY() {
        return this.getScrollAmount();
    }

    @Override
    public int tms$getScrollBarX() {
        return this.getScrollbarX();
    }
}

