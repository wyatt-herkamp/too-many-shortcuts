package dev.kingtux.tms.gui

import dev.kingtux.tms.TooManyShortcuts
import dev.kingtux.tms.api.clearBinding
import dev.kingtux.tms.api.hasModifiedKeyBindings
import dev.kingtux.tms.api.isShiftKey
import dev.kingtux.tms.api.modifiers.KeyModifier
import dev.kingtux.tms.api.modifiers.KeyModifier.Companion.fromKey
import dev.kingtux.tms.api.modifiers.KeyModifier.Companion.fromKeyCode
import dev.kingtux.tms.api.resetBinding
import dev.kingtux.tms.config.ConfigManager
import dev.kingtux.tms.mlayout.IKeyBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.option.ControlsListWidget.KeyBindingEntry
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.DirectionalLayoutWidget
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import org.apache.commons.lang3.ArrayUtils
import org.apache.logging.log4j.Level
import org.jetbrains.annotations.ApiStatus
import org.lwjgl.glfw.GLFW
import java.util.*

private val TITLE_TEXT: Text = Text.translatable("controls.keybinds.title")

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
class TMSKeyBindsScreen(parent: Screen, gameOptions: GameOptions) :
    GameOptionsScreen(parent, gameOptions, TITLE_TEXT) {

    var isShiftEnabled = false;
    var selectedKeyBinding: TMSKeyBindingEntry? = null
    lateinit var controlsList: TMSControlsListWidget;
    private var resetAllButton: ButtonWidget = ButtonWidget.builder(
        Text.translatable("controls.resetAll"),
    ) {
        gameOptions.allKeys.forEach {
            it.resetBinding(true)
        }
        controlsList.update()
    }.build()

    override fun init() {
        this.controlsList =
            layout.addBody(TMSControlsListWidget(this, this.client!!))
        super.init()
    }

    override fun initFooter() {
        val directionalLayoutWidget = layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8))
        directionalLayoutWidget.add(this.resetAllButton)
        directionalLayoutWidget.add(
            ButtonWidget.builder(
                ScreenTexts.DONE
            ) { this.close() }.build()
        )
    }

    override fun initTabNavigation() {
        layout.refreshPositions()
        controlsList.position(this.width, this.layout)
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        val config = ConfigManager.instance();
        if (this.selectedKeyBinding == null || !config.config.scrollBindings) {
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
        }
        this.selectedKeyBinding!!.updateMouseScroll(horizontalAmount, verticalAmount)
        controlsList.update()
        return true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (this.selectedKeyBinding == null) {
            return super.mouseClicked(mouseX, mouseY, button)
        }
        this.selectedKeyBinding!!.updateMouseClick(button)
        controlsList.update()
        return true
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (isShiftKey(keyCode)) isShiftEnabled = true
        if (this.selectedKeyBinding == null) {
            return super.keyPressed(keyCode, scanCode, modifiers)
        }
        // If the key is escape we are going to clear the selected binding.
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.selectedKeyBinding?.binding?.clearBinding(false)
            this.selectedKeyBinding = null
            controlsList.update()
            return true
        }
        this.selectedKeyBinding!!.updateKeyboardInput(keyCode, scanCode, modifiers)
        controlsList.update()
        return true
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (isShiftKey(keyCode)) isShiftEnabled = false

        if (this.selectedKeyBinding == null) {
            return super.keyPressed(keyCode, scanCode, modifiers)
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return true
        }
        this.selectedKeyBinding = null
        return true
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        resetAllButton.active = gameOptions.hasModifiedKeyBindings()
    }
}

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
class TMSControlsListWidget(val parent: TMSKeyBindsScreen, val client: MinecraftClient) :
    ElementListWidget<TMSControlListEntry?>(
        client,
        parent.width,
        parent.layout.contentHeight,
        parent.layout.headerHeight,
        20
    ) {
    private val searchField: SearchField = SearchField(this)
    private var maxKeyNameLength = 0

    init {
        this.addEntry(searchField)
        val (entries, maxKeyNameLength) = createAllEntries()
        this.maxKeyNameLength = maxKeyNameLength
        entries.forEach {
            this.addEntry(it)
        }
        update()
    }

    /**
     * Create all the entries for the controls list
     * @return Pair of the entries and the max key name length
     */
    fun createAllEntries(): Pair<List<TMSControlListEntry>, Int> {
        val entries = mutableListOf<TMSControlListEntry>()
        var currentCategory: String? = null
        val keyBindings = ArrayUtils.clone(client.options.allKeys as Array<KeyBinding>)
        var maxKeyNameLength = 0;
        Arrays.sort(keyBindings)
        for (keyBinding in keyBindings) {
            if (keyBinding.category != currentCategory) {
                currentCategory = keyBinding.category
                entries.add(TMSCategoryEntry(Text.translatable(currentCategory, client), this))
            }
            val bindingAsIKeyBinding = keyBinding as IKeyBinding
            if (bindingAsIKeyBinding.`tms$isAlternative`()) {
                continue;
            }
            val entry = TMSKeyBindingParentEntry(keyBinding, this);

            val textWidth = entry.getWidth(client.textRenderer)
            if (textWidth > maxKeyNameLength) {
                maxKeyNameLength = textWidth
            }
            entries.add(entry)
            // By Skipping the alternatives we can add them directly after the parent entry. This ensures that they are in the correct order.
            if (bindingAsIKeyBinding.`tms$hasAlternatives`()) {
                for (alternative in bindingAsIKeyBinding.`tms$getAlternatives`()) {
                    entries.add(TMSAlternativeKeyBindingEntry(alternative, this))
                }
            }

        }
        return Pair(entries, maxKeyNameLength)
    }


    fun update() {
        KeyBinding.updateKeysByCode()
        children().forEach() {
            it?.update()
        }
    }

    fun addAlternativeEntry(parent: TMSKeyBindingParentEntry, entry: KeyBinding) {
        val entries = children() as MutableList<TMSControlListEntry?>
        for (i in 0..<entries.size) {
            if (entries[i] === parent) {
                val parentKeyBinding = parent.binding as IKeyBinding
                val index = i + parentKeyBinding.`tms$getAlternativesCount`();
                TooManyShortcuts.log(Level.DEBUG, "Adding alternative at $index for ${parent.binding.translationKey}")
                entries.add(index, TMSAlternativeKeyBindingEntry(entry, this))
            }
        }
    }


    fun removeAlternativeEntry(entry: TMSAlternativeKeyBindingEntry) {
        this.removeEntry(entry)
    }

    override fun getRowWidth(): Int {
        return 340
    }
}

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
abstract class TMSControlListEntry(val parent: TMSControlsListWidget) :
    ElementListWidget.Entry<TMSControlListEntry?>() {
    abstract fun update()
}