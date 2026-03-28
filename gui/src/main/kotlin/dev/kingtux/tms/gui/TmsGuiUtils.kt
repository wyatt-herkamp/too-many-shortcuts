package dev.kingtux.tms.gui

import net.minecraft.client.resources.language.I18n
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting

const val DESCRIPTION_SUFFIX = ".${TmsGUI.MOD_ID}.description"
fun entryName(): Component = Component.literal("    ->");
fun resetTooltip(): Component = Component.translatable("tms_gui.options.controls.reset.tooltip")

val NO_RESULTS_TEXT: MutableComponent = Component.translatable("tms_gui.search.no_results")
    .run {
        setStyle(this.style.withColor(ChatFormatting.GRAY))
    };

val SUGGESTION_TEXT: String = I18n.get("tms_gui.search.placeholder");
