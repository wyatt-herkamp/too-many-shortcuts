package dev.kingtux.tms.gui

import com.google.common.collect.ImmutableList
import dev.kingtux.tms.alternatives.AlternativeKeyBinding
import dev.kingtux.tms.api.resetBinding
import dev.kingtux.tms.mlayout.IGameOptions
import dev.kingtux.tms.mlayout.IKeyBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.resource.language.I18n
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.Formatting
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.Level
import org.jetbrains.annotations.ApiStatus
import java.util.function.Supplier

@ApiStatus.Internal
abstract class TMSKeyBindingEntry(
    override val binding: KeyBinding,
    parent: TMSControlsListWidget,
) : TMSControlListEntry(parent), KeyBindingEntry<TMSControlsListWidget> {
    override var setModifierLast = false
    abstract val bindingName: Text;
    abstract val alternativesButton: ButtonWidget
    override var duplicate = false
    private val description: MutableList<Text> = mutableListOf()
    private val editButton: ButtonWidget =
        ButtonWidget.builder(Text.translatable(binding.translationKey)) {
            parent.parent.selectedKeyBinding = this
            parent.update()
        }
            .dimensions(0, 0, 75, 20)
            .narrationSupplier { textSupplier: Supplier<MutableText> ->
                editNarration(textSupplier)
            }
            .build()

    private fun editNarration(textSupplier: Supplier<MutableText>): MutableText {
        return if (binding.isUnbound) {
            Text.translatable("narrator.controls.unbound", bindingName)
        } else {
            Text.translatable("narrator.controls.bound", bindingName, textSupplier.get())
        }
    }

    private val resetButton: ButtonWidget = ButtonWidget.builder(
        Text.translatable("controls.reset")
    ) {
        binding.resetBinding(parent.parent.isShiftEnabled)
        parent.update()
    }.dimensions(0, 0, 50, 20).narrationSupplier {
        Text.translatable(
            "narrator.controls.reset",
            bindingName
        )
    }.build()
        .run {
            this.tooltip = Tooltip.of(resetTooltip())
            this
        }

    init {
        val descriptionKey = binding.translationKey + DESCRIPTION_SUFFIX
        if (I18n.hasTranslation(descriptionKey)) {
            val lines = StringUtils.split(I18n.translate(descriptionKey), '\n')
            for (line in lines) {
                description.add(Text.literal(line))
            }
        }
        this.update()
    }

    override fun update() {
        editButton.message = binding.boundKeyLocalizedText
        resetButton.active = (binding as IKeyBinding).`tms$canBeReset`()
        val mutableText = this.updateDuplicates();
        if (this.duplicate) {
            editButton.message =
                Text.literal("[ ").append(editButton.message.copy().formatted(Formatting.WHITE)).append(" ]").formatted(
                    Formatting.RED
                )
            editButton.tooltip = Tooltip.of(Text.translatable("controls.keybinds.duplicateKeybinds", mutableText))

        } else {
            editButton.tooltip = null
        }

        if (parent.parent.selectedKeyBinding === this) {
            editButton.message = Text.literal("> ")
                .append(editButton.message.copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                .append(" <")
                .formatted(Formatting.YELLOW)
        }
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
        val resetX: Int = parent.scrollbarX - resetButton.width - 40
        val yPos = y - 2
        resetButton.setPosition(resetX, yPos)
        resetButton.render(context, mouseX, mouseY, tickDelta)
        val editX = resetX - 5 - editButton.width
        editButton.setPosition(editX, yPos)
        editButton.render(context, mouseX, mouseY, tickDelta)

        context.drawTextWithShadow(
            parent.client.textRenderer,
            this.bindingName, x, y + entryHeight / 2 - 9 / 2, Colors.WHITE
        )
        if (this.duplicate) {
            val m = (editButton.x - 6)
            context.fill(m, y - 1, m + 3, y + entryHeight, -65536)
        }
        if (description.isNotEmpty() && mouseY >= y && mouseY < y + entryHeight && mouseX < editButton.x) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, description, mouseX, mouseY)
        }
        alternativesButton.y = resetButton.y
        alternativesButton.x = resetButton.x + resetButton.width + 10
        alternativesButton.render(context, mouseX, mouseY, tickDelta)
    }

    override fun children(): MutableList<out Element> {
        return ImmutableList.of(
            resetButton,
            editButton,
            alternativesButton
        )
    }

    override fun selectableChildren(): MutableList<out Selectable> {
        return ImmutableList.of(
            resetButton,
            editButton,
            alternativesButton
        )
    }

    fun getTranslationKey(): String {
        return binding.translationKey
    }

    override fun getWidth(renderer: TextRenderer): Int {
        return renderer.getWidth(bindingName)
    }

    fun entryKeyMatches(keyFilter: String?): Boolean {
        if (keyFilter == null) {
            return true
        }
        return when (keyFilter) {
            "" -> this.binding.isUnbound
            else -> StringUtils.containsIgnoreCase(
                binding.boundKeyLocalizedText.string,
                keyFilter
            )
        }
    }
}

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
class TMSKeyBindingParentEntry constructor(
    binding: KeyBinding,
    parent: TMSControlsListWidget,
) : TMSKeyBindingEntry(binding, parent) {
    override val bindingName: Text = Text.translatable(binding.translationKey)
    override val alternativesButton: ButtonWidget = ButtonWidget.builder(
        Text.literal("+")
    ) {
        val altBinding = AlternativeKeyBinding(binding)
        (MinecraftClient.getInstance().options as IGameOptions).registerKeyBinding(
            altBinding
        )
        parent.addAlternativeEntry(this, altBinding)
    }.size(20, 20).build()

    override fun equals(other: Any?): Boolean {
        if (other !is TMSKeyBindingParentEntry) return false
        if (binding != other.binding) return false
        return true
    }

    override fun hashCode(): Int {
        var result = bindingName.hashCode()
        result = 31 * result + alternativesButton.hashCode()
        return result
    }
}

@ApiStatus.Internal
class TMSAlternativeKeyBindingEntry(
    binding: KeyBinding,
    parent: TMSControlsListWidget,
) : TMSKeyBindingEntry(binding, parent) {
    override val bindingName: Text = entryName();
    override val alternativesButton: ButtonWidget = ButtonWidget.builder(
        Text.literal("x")
    ) {
        val iKeyBinding = binding as IKeyBinding
        TmsGUI.log(Level.INFO, "Removing Alternative")
        (iKeyBinding.`tms$getParent`() as IKeyBinding).`tms$removeAlternative`(binding)
        (MinecraftClient.getInstance().options as IGameOptions).removeKeyBinding(binding)
        parent.removeAlternativeEntry(this)
    }.size(20, 20).build();

}