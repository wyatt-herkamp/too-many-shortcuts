package dev.kingtux.tms.gui

import dev.kingtux.tms.api.clearBinding
import dev.kingtux.tms.api.hasModifiedKeyBindings
import dev.kingtux.tms.api.isShiftKey
import dev.kingtux.tms.api.resetBinding
import dev.kingtux.tms.mlayout.IKeyBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.options.OptionsSubScreen
import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.Options
import net.minecraft.client.KeyMapping
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import org.apache.logging.log4j.Level
import org.jetbrains.annotations.ApiStatus
import org.lwjgl.glfw.GLFW

private val TITLE_TEXT: Component = Component.translatable("controls.keybinds.title")

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
class TMSKeyBindsScreen(parent: Screen, gameOptions: Options) :
    OptionsSubScreen(parent, gameOptions, TITLE_TEXT),
    KeyBindingScreenType<TMSKeyBindsScreen, TMSKeyBindingEntry, TMSControlsListWidget> {
    override var isShiftEnabled = false;
    override var selectedKeyBinding: TMSKeyBindingEntry? = null
    lateinit var controlsList: TMSControlsListWidget;
    private var resetAllButton: Button = Button.builder(
        Component.translatable("controls.resetAll"),
    ) {
        options.keyMappings.forEach {
            it.resetBinding(true)
        }
        controlsList.update()
    }.build()
    private lateinit var searchField: EditBox;
    override var lastKeyCodeUpdateTime: Long = 0L
    override fun client(): Minecraft {
        return this.minecraft!!
    }

    override fun setSelectedKeyBindingToNull() {
        this.selectedKeyBinding = null
    }

    override var show: ShowOptions = ShowOptions.SHOW_ALL
    override var screenMode: ScreenModes = ScreenModes.KeyBindings
    private val changeScreenMode: Button = Button.builder(
        screenMode.buttonText(),
    ) {
        screenMode = screenMode.next()
        it.message = screenMode.buttonText()
        controlsList.rebuildEntries(searchField.value);
    }.build()
    val showButton: Button = Button.builder(
        Component.translatable(show.fullTranslationKey()),
    ) {
        show = show.next()
        it.message = show.text()
        TmsGUI.log(Level.DEBUG, "Show: $show")
        controlsList.rebuildEntries(searchField.value);
    }.build()


    override fun addContents() {
        this.controlsList =
            layout.addToContents(TMSControlsListWidget(this, this.minecraft!!))
    }

    override fun gameOptions(): Options {
        return this.options;
    }

    override fun addOptions() {
    }

    override fun addTitle() {
        searchField = EditBox(
            this.font,
            Minecraft.getInstance().screen!!.width / 2 - 125,
            0,
            250,
            20,
            Component.empty()
        );
        searchField.setSuggestion(SUGGESTION_TEXT)
        searchField.setResponder {
            search(it)
        }
        val linearLayout = layout.addToHeader(LinearLayout.horizontal().spacing(8))
        linearLayout.addChild(StringWidget(this.title, this.font))
        linearLayout.addChild(searchField);
    }

    override fun addFooter() {
        layout.setFooterHeight(40)
        val linearLayout = layout.addToFooter(LinearLayout.vertical().spacing(2))
        val rowOne = LinearLayout.horizontal().spacing(8)
        rowOne.addChild(this.changeScreenMode)
        rowOne.addChild(this.showButton)
        linearLayout.addChild(rowOne)
        val rowTwo = LinearLayout.horizontal().spacing(8)
        rowTwo.addChild(this.resetAllButton)
        rowTwo.addChild(
            Button.builder(
                CommonComponents.GUI_DONE
            ) { this.onClose() }.build()
        )
        linearLayout.addChild(rowTwo)
    }

    override fun repositionElements() {
        layout.arrangeElements()
        controlsList.updateSize(this.width, this.layout)
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



    override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
        if (this.selectedKeyBinding == null) {
            return super.mouseClicked(click, doubled)
        }
        this.selectedKeyBinding!!.updateMouseClick(click.button())
        controlsList.update()
        return true
    }

    override fun keyPressed(input: KeyEvent): Boolean {
        if (isShiftKey(input.key)) isShiftEnabled = true
        if (this.selectedKeyBinding == null) {
            return super.keyPressed(input)
        }
        // If the key is escape we are going to clear the selected binding.
        if (input.key == GLFW.GLFW_KEY_ESCAPE) {
            this.selectedKeyBinding?.binding?.clearBinding(false)
            controlsList.update()
            return true
        }
        this.selectedKeyBinding!!.updateKeyboardInput(input)
        controlsList.update()
        return true
    }

    override fun keyReleased(input: KeyEvent): Boolean {
        if (isShiftKey(input.key)) isShiftEnabled = false

        if (this.selectedKeyBinding == null) {
            return super.keyReleased(input)
        }

        this.selectedKeyBinding = null
        return true
    }


    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        super.extractRenderState(context, mouseX, mouseY, delta)
        resetAllButton.active = options.hasModifiedKeyBindings()
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
class TMSControlsListWidget(override val parent: TMSKeyBindsScreen, client: Minecraft) :
    ContainerObjectSelectionList<TMSControlListEntry>(
        client,
        parent.width,
        parent.layout.getContentHeight(),
        parent.layout.getHeaderHeight(),
        20
    ), ControlsListWidget<TMSControlsListWidget, TMSKeyBindingEntry, TMSKeyBindsScreen> {
    override var maxKeyNameLength = 0

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
        for (key in InputConstants.Key.NAME_MAP.entries) {
            val inputKey: InputConstants.Key = key.value
            val entry = KeyFreeListEntry(inputKey, this)
            addEntry(entry)
        }
    }

    private fun rebuildKeybindingEntries(searchValue: String?) {
        val (entries, maxKeyNameLength) = createAllEntries(searchValue)
        this.maxKeyNameLength = maxKeyNameLength
        setScrollAmount(0.0)
        var lastCategory: KeyMapping.Category? = null
        if (entries.isEmpty()) {
            addEntry(TMSCategoryEntry(NO_RESULTS_TEXT, this))
        } else {
            for (entry in entries) {
                if (entry is TMSKeyBindingParentEntry) {
                    val category = entry.binding.category
                    if (category != lastCategory) {
                        lastCategory = category
                        addEntry(TMSCategoryEntry(category.label(), this))
                    }
                }
                addEntry(entry)
            }
        }
    }

    override fun createEntry(binding: KeyMapping, alternative: Boolean): TMSKeyBindingEntry {
        return if (alternative) {
            TMSAlternativeKeyBindingEntry(binding, this)
        } else {
            TMSKeyBindingParentEntry(binding, this)
        }
    }


    fun update() {
        KeyMapping.resetMapping()
        children().forEach() {
            it?.update()
        }
    }


    fun removeAlternativeEntry(entry: TMSAlternativeKeyBindingEntry) {
        this.removeEntry(entry)
    }

    override fun getRowWidth(): Int {
        return 340
    }

    fun getScrollBarX(): Int {
        return scrollBarX()
    }
}

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
abstract class TMSControlListEntry(val parent: TMSControlsListWidget) :
    ContainerObjectSelectionList.Entry<TMSControlListEntry>() {
    abstract fun update()
}
