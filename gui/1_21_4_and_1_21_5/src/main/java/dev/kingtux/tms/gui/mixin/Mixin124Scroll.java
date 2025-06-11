package dev.kingtux.tms.gui.mixin;

import dev.kingtux.tms.gui.mlayout.IScrollMixin;
import dev.kingtux.tms.mixin.helpers.MinecraftVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@MinecraftVersion(
        minecraftVersions = {"1.21.4", "1.21.5"})
@Environment(EnvType.CLIENT)
@Mixin(ScrollableWidget.class)
public abstract class Mixin124Scroll implements IScrollMixin {

    @Shadow public abstract void setScrollY(double scrollY);

    @Shadow public abstract double getScrollY();

    @Shadow protected abstract int getScrollbarX();

    @Override
    public double tms$getScrollY() {
        return this.getScrollY();
    }
    @Override
    public void tms$setScrollY(double scrollY) {
        this.setScrollY(scrollY);
    }
    @Override
    public int tms$getScrollBarX() {
        return getScrollbarX();
    }
}
