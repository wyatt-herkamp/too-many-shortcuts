package dev.kingtux.tms.mlayout;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;

public interface IKeyBindingEntry {
    KeyBinding tms$getKeyBinding();

    ButtonWidget tms$getEditButton();
}
