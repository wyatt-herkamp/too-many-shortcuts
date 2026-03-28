package dev.kingtux.tms.gui

import com.google.common.collect.ImmutableList
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.ComponentPath
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.navigation.FocusNavigationEvent
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.narration.NarratedElementType

import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
class TMSCategoryEntry(
    val text: Component, parent: TMSControlsListWidget,
) : TMSControlListEntry(parent) {
    private val textWidth = parent.minecraft.font.width(this.text)

    override fun extractContent(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, hovered: Boolean, deltaTicks: Float) {
        // textX is used to center the category
        val textX = parent.width / 2 - this.textWidth / 2;
        context.text(
            parent.minecraft.font,
            this.text,
            textX,
            y,
            CommonColors.WHITE,
            false
        )
    }

    override fun nextFocusPath(navigation: FocusNavigationEvent): ComponentPath? {
        return null
    }

    override fun children(): List<GuiEventListener> {
        return listOf()
    }

    override fun narratables(): List<NarratableEntry> {
        return ImmutableList.of(object : NarratableEntry {
            override fun narrationPriority(): NarratableEntry.NarrationPriority {
                return NarratableEntry.NarrationPriority.HOVERED
            }

            override fun updateNarration(builder: NarrationElementOutput) {
                builder.add(NarratedElementType.TITLE, text)
            }
        })
    }

    override fun update() {
    }
}
