package dev.kingtux.tms.gui.mlayout;

import net.minecraft.client.gui.tooltip.Tooltip;
import org.jetbrains.annotations.Nullable;

public interface IToolTip {
    void tms$setToolTip(@Nullable Tooltip toolTip);

    default void tms$removeToolTip(){
        tms$setToolTip(null);
    }
}
