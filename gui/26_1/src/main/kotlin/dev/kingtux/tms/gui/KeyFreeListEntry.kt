package dev.kingtux.tms.gui

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors

class KeyFreeListEntry(
    val key: InputConstants.Key, parent: TMSControlsListWidget,
) : TMSControlListEntry(parent) {
    val translationKey: Component = Component.translatable(key.name);
    val keyText: Component = Component.literal(key.toString());
    override fun update() {

    }

    override fun extractContent(
        context: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        deltaTicks: Float
    ) {
        context.text(
            parent.minecraft.font,
            this.translationKey,
            x  - parent.minecraft.font.width(this.keyText) - 5,
            y  / 2 - 9 / 2,
            CommonColors.WHITE,
            true
        )
        context.text(
            parent.minecraft.font,
            this.keyText, x, y / 2 - 9 / 2, CommonColors.WHITE,
            true
        )
    }
    override fun children(): List<GuiEventListener> {
        return listOf()
    }

    override fun narratables(): MutableList<out NarratableEntry> {
        return mutableListOf()
    }
}
