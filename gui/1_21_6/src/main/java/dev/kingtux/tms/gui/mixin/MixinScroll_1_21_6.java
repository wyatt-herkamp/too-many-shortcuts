package dev.kingtux.tms.gui.mixin;

import dev.kingtux.tms.gui.mlayout.IToolTip;
import dev.kingtux.tms.mixin.helpers.MinecraftVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@MinecraftVersion(
        minecraftVersions = {"1.21.6-rc.1","1.21.6"})
@Environment(EnvType.CLIENT)
@Mixin(ClickableWidget.class)
public abstract class MixinScroll_1_21_6 implements IToolTip {
    @Shadow public abstract void setTooltip(@Nullable Tooltip tooltip);

    @Override
    public void tms$setToolTip(@Nullable Tooltip toolTip) {
        this.setTooltip(toolTip);
    }
}
