package dev.kingtux.tms.gui

import dev.kingtux.tms.api.clearBinding
import dev.kingtux.tms.api.hasModifiedKeyBindings
import dev.kingtux.tms.api.isShiftKey
import dev.kingtux.tms.api.resetBinding
import dev.kingtux.tms.mlayout.IKeyBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.*
import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import org.apache.logging.log4j.Level
import org.jetbrains.annotations.ApiStatus
import org.lwjgl.glfw.GLFW

private val TITLE_TEXT: Text = Text.translatable("controls.keybinds.title")

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
class TMSKeyBindsScreen(parent: Screen, gameOptions: GameOptions) :
    GameOptionsScreen(parent, gameOptions, TITLE_TEXT),
    KeyBindingScreenType<TMSKeyBindsScreen, TMSKeyBindingEntry, TMSControlsListWidget> {
    override var isShiftEnabled = false;
    override var selectedKeyBinding: TMSKeyBindingEntry? = null
    lateinit var controlsList: TMSControlsListWidget;
    private var resetAllButton: ButtonWidget = ButtonWidget.builder(
        Text.translatable("controls.resetAll"),
    ) {
        gameOptions.allKeys.forEach {
            it.resetBinding(true)
        }
        controlsList.update()
    }.build()
    private lateinit var searchField: TextFieldWidget;
    override var lastKeyCodeUpdateTime: Long = 0L

    override fun setSelectedKeyBindingToNull() {
        this.selectedKeyBinding = null
    }

    override var show: ShowOptions = ShowOptions.SHOW_ALL
    override var screenMode: ScreenModes = ScreenModes.KeyBindings
    private val changeScreenMode: ButtonWidget = ButtonWidget.builder(
        screenMode.buttonText(),
    ) {
        screenMode = screenMode.next()
        it.message = screenMode.buttonText()
        controlsList.rebuildEntries(searchField.text);
    }.build()
    val showButton: ButtonWidget = ButtonWidget.builder(
        Text.translatable(show.fullTranslationKey()),
    ) {
        show = show.next()
        it.message = show.text
        TmsGUI.log(Level.DEBUG, "Show: $show")
        controlsList.rebuildEntries(searchField.text);
    }.build()


    override fun initBody() {
        this.controlsList =
            layout.addBody(TMSControlsListWidget(this, this.client!!))
    }

    override fun gameOptions(): GameOptions {
        return this.gameOptions;
    }

    override fun addOptions() {
    }

    override fun initHeader() {
        searchField = TextFieldWidget(
            this.textRenderer,
            MinecraftClient.getInstance().currentScreen!!.width / 2 - 125,
            0,
            250,
            20,
            Text.empty()
        );
        searchField.setSuggestion(SUGGESTION_TEXT)
        searchField.setChangedListener {
            search(it)
        }
        val directionalLayoutWidget = layout.addHeader(DirectionalLayoutWidget.horizontal().spacing(8))
        directionalLayoutWidget.add(TextWidget(this.title, this.textRenderer))
        directionalLayoutWidget.add(searchField);
    }

    override fun initFooter() {
        layout.footerHeight = 40
        val directionalLayoutWidget = layout.addFooter(DirectionalLayoutWidget.vertical().spacing(2))
        val rowOne = DirectionalLayoutWidget.horizontal().spacing(8)
        rowOne.add(this.changeScreenMode)
        rowOne.add(this.showButton)
        directionalLayoutWidget.add(rowOne)
        val rowTwo = DirectionalLayoutWidget.horizontal().spacing(8)
        rowTwo.add(this.resetAllButton)
        rowTwo.add(
            ButtonWidget.builder(
                ScreenTexts.DONE
            ) { this.close() }.build()
        )
        directionalLayoutWidget.add(rowTwo)
    }

    override fun client(): MinecraftClient {
        return this.client!!
    }

    override fun refreshWidgetPositions() {
        layout.refreshPositions()
        controlsList.position(this.width, this.layout)
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        if (this.selectedKeyBinding == null) {
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
        }
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

        this.selectedKeyBinding = null
        return true
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        resetAllButton.active = gameOptions.hasModifiedKeyBindings()
    }

    fun search(text: String) {
        val searchText = text.trim()
        if (searchText.isEmpty()) {
            searchField.setSuggestion(SUGGESTION_TEXT)
        } else {
            searchField.setSuggestion("")
        }
        controlsList.rebuildEntries(searchText)
    }
}

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
class TMSControlsListWidget(override val parent: TMSKeyBindsScreen, client: MinecraftClient) :
    ElementListWidget<TMSControlListEntry?>(
        client,
        parent.width,
        parent.layout.contentHeight,
        parent.layout.headerHeight,
        20
    ), ControlsListWidget<TMSControlsListWidget, TMSKeyBindingEntry, TMSKeyBindsScreen> {
    override var maxKeyNameLength = 0

    override fun createEntry(
        binding: KeyBinding,
        alternative: Boolean
    ): TMSKeyBindingEntry {
        return if (alternative) {
            TMSAlternativeKeyBindingEntry(binding, this)
        } else {
            TMSKeyBindingParentEntry(binding, this)
        }
    }

    init {
        this.rebuildEntries(null)
    }

    fun rebuildEntries(searchValue: String?) {
        clearEntries()
        when (parent.screenMode) {
            ScreenModes.KeyBindings -> {
                rebuildKeybindingEntries(searchValue)
            }

            ScreenModes.FreeList -> {
                rebuildFreeListEntries(searchValue)
            }
        }
        update()
    }

    private fun rebuildFreeListEntries(searchValue: String?) {
        for (key in InputUtil.Key.KEYS.entries) {
            val inputKey: InputUtil.Key = key.value
            val entry = KeyFreeListEntry(inputKey, this)
            addEntry(entry)
        }
    }

    private fun rebuildKeybindingEntries(searchValue: String?) {
        val (entries, maxKeyNameLength) = createAllEntries(searchValue)
        this.maxKeyNameLength = maxKeyNameLength
        scrollY = 0.0
        var lastCategory: String? = null
        if (entries.isEmpty()) {
            addEntry(TMSCategoryEntry(NO_RESULTS_TEXT, this))
        } else {
            for (entry in entries) {
                if (entry is TMSKeyBindingParentEntry) {
                    val category = entry.binding.category
                    if (category != lastCategory) {
                        lastCategory = category
                        addEntry(TMSCategoryEntry(Text.translatable(category), this))
                    }
                }
                addEntry(entry)
            }
        }
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
                TmsGUI.log(Level.DEBUG, "Adding alternative at $index for ${parent.binding.translationKey}")
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