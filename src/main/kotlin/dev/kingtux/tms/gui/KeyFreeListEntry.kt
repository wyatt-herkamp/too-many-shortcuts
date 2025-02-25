package dev.kingtux.tms.gui

import com.google.common.collect.ImmutableList
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.Colors

class KeyFreeListEntry(
    val key: InputUtil.Key, parent: TMSControlsListWidget,
) : TMSControlListEntry(parent)  {
     val translationKey: Text = Text.translatable(key.translationKey);
     val keyText: Text = Text.of(key.toString());
    override fun update() {

    }

    override fun render(
        context: DrawContext,
        index: Int,
        y: Int,
        x: Int,
        entryWidth: Int,
        entryHeight: Int,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        tickDelta: Float
    ) {
        context.drawTextWithShadow(
            parent.client.textRenderer,
            this.translationKey, x + entryWidth - parent.client.textRenderer.getWidth(this.keyText) - 5, y + entryHeight / 2 - 9 / 2, Colors.WHITE
        )
        context.drawTextWithShadow(
            parent.client.textRenderer,
            this.keyText, x, y + entryHeight / 2 - 9 / 2, Colors.WHITE
        )
    }

    override fun children(): MutableList<out Element> {
        return mutableListOf(
            translationKey as Element,
            keyText as Element
        )
    }

    override fun selectableChildren(): MutableList<out Selectable> {
        return mutableListOf()
    }
}