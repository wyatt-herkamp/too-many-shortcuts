package dev.kingtux.tms.gui

import dev.kingtux.tms.alternatives.AlternativeKeyBinding
import dev.kingtux.tms.api.resetBinding
import dev.kingtux.tms.mlayout.IGameOptions
import dev.kingtux.tms.mlayout.IKeyBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.Button
import net.minecraft.client.KeyMapping
import net.minecraft.client.resources.language.I18n
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.ChatFormatting
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.Level
import org.jetbrains.annotations.ApiStatus
import java.util.Optional
import java.util.function.Supplier

@ApiStatus.Internal
abstract class TMSKeyBindingEntry(
    override val binding: KeyMapping,
    parent: TMSControlsListWidget,
) : TMSControlListEntry(parent), KeyBindingEntry<TMSControlsListWidget> {
    override var setModifierLast = false
    abstract val bindingName: Component;
    abstract val alternativesButton: Button
    override var duplicate = false
    private val description: MutableList<Component> = mutableListOf()
    private val editButton: Button =
        Button.builder(Component.translatable(binding.name)) {
            parent.parent.selectedKeyBinding = this
            parent.update()
        }
            .bounds(0, 0, 75, 20)
            .createNarration { textSupplier: Supplier<MutableComponent> ->
                editNarration(textSupplier)
            }
            .build()

    private fun editNarration(textSupplier: Supplier<MutableComponent>): MutableComponent {
        return if (binding.isUnbound) {
            Component.translatable("narrator.controls.unbound", bindingName)
        } else {
            Component.translatable("narrator.controls.bound", bindingName, textSupplier.get())
        }
    }

    private val resetButton: Button = Button.builder(
        Component.translatable("controls.reset")
    ) {
        binding.resetBinding(parent.parent.isShiftEnabled)
        parent.update()
    }.bounds(0, 0, 50, 20).createNarration {
        Component.translatable(
            "narrator.controls.reset",
            bindingName
        )
    }.build()
        .run {
            this.setTooltip(Tooltip.create(resetTooltip()))
            this
        }

    init {
        val descriptionKey = binding.name + DESCRIPTION_SUFFIX
        if (I18n.exists(descriptionKey)) {
            val lines = StringUtils.split(I18n.get(descriptionKey), '\n')
            for (line in lines) {
                description.add(Component.literal(line))
            }
        }
        this.update()
    }

    override fun update() {
        editButton.message = binding.translatedKeyMessage
        resetButton.active = (binding as IKeyBinding).`tms$canBeReset`()
        val mutableText = this.updateDuplicates();
        if (this.duplicate) {
            editButton.message =
                Component.literal("[ ").append(editButton.message.copy().withStyle(ChatFormatting.WHITE)).append(" ]").withStyle(
                    ChatFormatting.YELLOW
                )
            editButton.setTooltip(
                Tooltip.create(Component.translatable("controls.keybinds.duplicateKeybinds", mutableText!!))
            )
        } else {
            editButton.setTooltip(null)
        }

        if (parent.parent.selectedKeyBinding === this) {
            editButton.message = Component.literal("> ")
                .append(editButton.message.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
                .append(" <")
                .withStyle(ChatFormatting.YELLOW)
        }
    }

    override fun extractContent(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, hovered: Boolean, deltaTicks: Float) {
        val resetX: Int = parent.getScrollBarX() - resetButton.width - 40
        val yPos = y - 2
        resetButton.setX(resetX)
        resetButton.setY(yPos)
        resetButton.extractRenderState(context, mouseX, mouseY, deltaTicks)
        val editX = resetX - 5 - editButton.width
        editButton.setX(editX)
        editButton.setY(yPos)
        editButton.extractRenderState(context, mouseX, mouseY, deltaTicks)

        context.text(
            parent.minecraft.font,
            this.bindingName, x, y, CommonColors.WHITE,
            true
        )
        // That yellow bar to the left of the edit button
        if (this.duplicate) {
            // Copied from Minecraft KeyBindingEntry
            val fillX = (editButton.x - 6)
            context.fill(fillX, contentY - 1, fillX + 3, contentBottom, CommonColors.YELLOW)
        }
        if (description.isNotEmpty() && mouseY >= y && mouseY < y && mouseX < editButton.x) {
            context.setTooltipForNextFrame(Minecraft.getInstance().font, description, Optional.empty(), mouseX, mouseY)
        }
        alternativesButton.setY(resetButton.y)
        alternativesButton.setX(resetButton.x + resetButton.width + 10)
        alternativesButton.extractRenderState(context, mouseX, mouseY, deltaTicks)
    }
    override fun children(): MutableList<out GuiEventListener> {
        return mutableListOf(
            resetButton,
            editButton,
            alternativesButton
        )
    }

    override fun narratables(): MutableList<out NarratableEntry> {
        return mutableListOf(
            resetButton,
            editButton,
            alternativesButton
        )
    }

    override fun getWidth(renderer: Font): Int {
        return renderer.width(bindingName)
    }

}

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
class TMSKeyBindingParentEntry constructor(
    binding: KeyMapping,
    parent: TMSControlsListWidget,
) : TMSKeyBindingEntry(binding, parent) {
    override val bindingName: Component = Component.translatable(binding.name)
    override val alternativesButton: Button = Button.builder(
        Component.literal("+")
    ) {
        val altBinding = AlternativeKeyBinding(binding)
        (Minecraft.getInstance().options as IGameOptions).registerKeyBinding(
            altBinding
        )
        parent.rebuildEntries(null)
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
    binding: KeyMapping,
    parent: TMSControlsListWidget,
) : TMSKeyBindingEntry(binding, parent) {
    override val bindingName: Component = entryName();
    override val alternativesButton: Button = Button.builder(
        Component.literal("x")
    ) {
        val iKeyBinding = binding as IKeyBinding
        TmsGUI.log(Level.INFO, "Removing Alternative")
        (iKeyBinding.`tms$getParent`() as IKeyBinding).`tms$removeAlternative`(binding)
        (Minecraft.getInstance().options as IGameOptions).removeKeyBinding(binding)
        parent.removeAlternativeEntry(this)
    }.size(20, 20).build();

}
