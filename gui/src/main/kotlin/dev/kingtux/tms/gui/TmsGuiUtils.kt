package dev.kingtux.tms.gui

import net.minecraft.client.resource.language.I18n
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

const val DESCRIPTION_SUFFIX = ".${TmsGUI.MOD_ID}.description"
fun entryName(): Text = Text.literal("    ->");
fun resetTooltip(): Text = Text.translatable("tms_gui.options.controls.reset.tooltip")

val NO_RESULTS_TEXT: MutableText = Text.translatable("tms_gui.search.no_results")
    .run {
        setStyle(this.style.withColor(Formatting.GRAY))
    };

val SUGGESTION_TEXT: String = I18n.translate("tms_gui.search.placeholder");
