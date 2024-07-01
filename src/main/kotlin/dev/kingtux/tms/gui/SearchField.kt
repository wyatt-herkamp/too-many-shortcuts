package dev.kingtux.tms.gui

import dev.kingtux.tms.TooManyShortcuts.log
import dev.kingtux.tms.api.equalsIgnoreCase
import dev.kingtux.tms.api.isAlternative
import dev.kingtux.tms.api.translatedTextEqualsIgnoreCase

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable

import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.resource.language.I18n
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.apache.logging.log4j.Level
import org.jetbrains.annotations.ApiStatus
import java.util.*

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
class SearchField(parent: TMSControlsListWidget) : TMSControlListEntry(parent) {
    companion object {
        private val SUGGESTION_TEXT = I18n.translate("too_many_shortcuts.search.placeholder");
        private val NO_RESULTS_TEXT = Text.translatable("too_many_shortcuts.search.no_results")
            .run {
                setStyle(this.style.withColor(Formatting.GRAY))
            };
    }

    private val entries: MutableSet<TMSKeyBindingEntry> =
        TreeSet(Comparator.comparing { o: TMSKeyBindingEntry -> o.binding })

    private val textFieldWidget: TextFieldWidget = TextFieldWidget(
        parent.client.textRenderer,
        parent.client.currentScreen!!.width / 2 - 125,
        0,
        250,
        20,
        Text.empty()
    ).run {
        this.setSuggestion(SUGGESTION_TEXT)
        this.setChangedListener {
            search(it)
        }
        this
    }
    private var lastEntryCount = 0

    fun search(text: String) {
        var searchText = text.trim()

        if (searchText.isEmpty()) {
            textFieldWidget.setSuggestion(SUGGESTION_TEXT)
        } else {
            textFieldWidget.setSuggestion("")
        }
        parent.scrollAmount = 0.0

        val children = parent.children();
        if (entries.isEmpty()) {
            children.forEach {
                if (it is TMSKeyBindingEntry) {
                    entries.add(it)
                }
            }
            lastEntryCount = children.size
        }
        val childrenCount = children.size
        if (childrenCount != lastEntryCount) {
            log(Level.INFO, "Controls search results changed externally - recompiling the list!")
            entries.clear()
            val keyBindings: Array<KeyBinding> = parent.client.options.allKeys
            Arrays.sort(keyBindings)
            var lastCat: String? = null
            var entry: TMSKeyBindingEntry
            lastEntryCount = 1
            for (keyBinding in keyBindings) {
                if (lastCat != keyBinding.category) {
                    lastCat = keyBinding.category
                    children.add(TMSCategoryEntry(Text.translatable(keyBinding.category), parent))
                    lastEntryCount++
                }
                entry = TMSKeyBindingEntry.newEntry(keyBinding, parent);
                children.add(entry)
                entries.add(entry)
                lastEntryCount++
            }
        }

        children.clear()
        children.add(this)

        var keyFilter: String? = null
        val keyDelimiterPos = searchText.indexOf('=')
        if (keyDelimiterPos == 0) {
            keyFilter = searchText.substring(1).trim { it <= ' ' }
            searchText = ""
        } else if (keyDelimiterPos > 0) {
            keyFilter = searchText.substring(keyDelimiterPos + 1).trim { it <= ' ' }
            searchText = searchText.substring(0, keyDelimiterPos).trim { it <= ' ' }
        }

        var lastCat: String? = null
        var lastMatched = false
        var includeCat = false
        lastEntryCount = 1
        for (entry in entries) {
            if (lastMatched && entry.binding.isAlternative()) {
                children.add(entry)
                lastEntryCount++
                continue
            }

            val cat = Text.translatable(entry.binding.category);
            if (cat.string != lastCat) {
                includeCat = cat.equalsIgnoreCase(searchText)
            }
            val categoryOrKeyMatches = (includeCat || entry.binding.translatedTextEqualsIgnoreCase(searchText));
            if (categoryOrKeyMatches && entry.entryKeyMatches(keyFilter)) {
                if (cat.string != lastCat) {
                    children.add(TMSCategoryEntry(cat, parent))
                    lastCat = cat.string
                    lastEntryCount++
                }
                children.add(entry)
                lastEntryCount++
                lastMatched = true
            } else {
                lastMatched = false
            }
        }
        if (lastEntryCount <= 1) {
            children.add(TMSCategoryEntry(NO_RESULTS_TEXT, parent))
        }
    }

    override fun children(): List<Element> {
        return listOf(textFieldWidget)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return textFieldWidget.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return textFieldWidget.mouseReleased(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return textFieldWidget.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(char: Char, modifiers: Int): Boolean {
        return textFieldWidget.charTyped(char, modifiers)
    }

    override fun setFocused(focussed: Boolean) {
        textFieldWidget.isFocused = focussed
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
        textFieldWidget.y = y
        textFieldWidget.render(context, mouseX, mouseY, tickDelta)
    }

    override fun selectableChildren(): List<Selectable?> {
        return listOf(textFieldWidget)
    }

    override fun update() {}
}
