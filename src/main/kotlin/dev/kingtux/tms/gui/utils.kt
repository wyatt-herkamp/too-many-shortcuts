package dev.kingtux.tms.gui

import dev.kingtux.tms.TooManyShortcuts
import net.minecraft.text.Text

const val DESCRIPTION_SUFFIX = ".${TooManyShortcuts.MOD_ID}.description"
fun entryName(): Text = Text.literal("    ->");
fun resetTooltip(): Text = Text.translatable("too_many_shortcuts.options.controls.reset.tooltip")

