package dev.kingtux.tms.gui

import com.google.common.collect.ImmutableList
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.navigation.GuiNavigation
import net.minecraft.client.gui.navigation.GuiNavigationPath
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart

import net.minecraft.text.Text
import net.minecraft.util.Colors
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
class TMSCategoryEntry(
    val text: Text, parent: TMSControlsListWidget,
) : TMSControlListEntry(parent) {
    private val textWidth = parent.client.textRenderer.getWidth(this.text)

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, hovered: Boolean, deltaTicks: Float) {
        // textX is used to center the category
        val textX = parent.width / 2 - this.textWidth / 2;
        context?.drawText(
            parent.client.textRenderer,
            this.text,
            textX,
            y,
            Colors.WHITE,
            false
        )
    }

    override fun getNavigationPath(navigation: GuiNavigation): GuiNavigationPath? {
        return null
    }

    override fun children(): List<Element> {
        return listOf()
    }

    override fun selectableChildren(): List<Selectable?> {
        return ImmutableList.of(object : Selectable {
            override fun getType(): Selectable.SelectionType {
                return Selectable.SelectionType.HOVERED
            }

            override fun appendNarrations(builder: NarrationMessageBuilder) {
                builder.put(NarrationPart.TITLE, text)
            }
        })
    }

    override fun update() {
    }
}
