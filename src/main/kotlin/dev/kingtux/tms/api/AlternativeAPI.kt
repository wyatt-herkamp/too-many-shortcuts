package dev.kingtux.tms.api

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import dev.kingtux.tms.TooManyShortcuts
import dev.kingtux.tms.alternatives.createAlternativeKeyBinding
import dev.kingtux.tms.mlayout.IGameOptions
import dev.kingtux.tms.mlayout.IKeyBinding
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import java.util.HashMap

object AlternativeAPI {
    val defaultAlternatives: Multimap<KeyBinding, KeyBinding> = Multimaps.newSetMultimap(HashMap()) { hashSetOf() };

    /**
     * Create an alternative keybinding with the given code and [InputUtil.Type.KEYSYM].
     *
     * @param base The base keybinding to create an alternative for
     * @param code The keycode to use as default for the alternative
     */
    fun create(base: KeyBinding?, code: Int) {
        create(base, InputUtil.Type.KEYSYM, code)
    }

    /**
     * Create an alternative keybinding with the given code and input type.
     *
     * @param base      The base keybinding to create an alternative for
     * @param inputType The [InputUtil.Type] that defines the type of the code
     * @param code      The input code
     */
    fun create(base: KeyBinding?, inputType: InputUtil.Type?, code: Int) {
        val alternative = createAlternativeKeyBinding(base!!, inputType, code)
        val gameOptions = (MinecraftClient.getInstance().options as IGameOptions)
        gameOptions.registerKeyBinding(alternative)
        defaultAlternatives.put(base, alternative)
    }

    /**
     * Register and add the latter keybinding to the former.<br></br>
     * This is useful when using more complex keybinding trigger, e.g. in use with Amces.<br></br>
     * The translation key and the category of the alternative keybinding will be rewritten
     * and as such it must not be registered yet.
     *
     * @param base        The base keybinding to create an alternative for
     * @param alternative The alternative keybinding. This keybinding MUST NOT be registered yet
     */
    fun create(base: KeyBinding, alternative: KeyBinding) {
        (alternative as IKeyBinding).`tms$setTranslationKey`(base.translationKey + "%" + (base as IKeyBinding).`tms$getNextChildId`())
        (alternative as IKeyBinding).`tms$setCategory`(base.category)
        (base as IKeyBinding).`tms$addAlternative`(alternative)
        (alternative as IKeyBinding).`tms$setParent`(base)
        val gameOptions = (MinecraftClient.getInstance().options as IGameOptions)

        gameOptions.registerKeyBinding(alternative)
        defaultAlternatives.put(base, alternative)
    }

    /**
     * Returns whether the given keybinding is an alternative.
     *
     * @param binding A keybinding
     * @return Whether the given keybinding is an alternative
     */
    fun isAlternative(binding: KeyBinding): Boolean {
        return (binding as IKeyBinding).`tms$isAlternative`()
    }

    /**
     * Gets all alternatives that are registered for a keybinding.
     *
     * @param binding A keyinding
     * @return A list of alternatives or `null`
     */
    fun getAlternatives(binding: KeyBinding): List<KeyBinding>? {
        return (binding as IKeyBinding).`tms$getAlternatives`()
    }

    /**
     * Gets the base keybinding for an alternative keybinding.
     *
     * @param binding An alternative keybinding
     * @return The base keyinding or `null` if the given keybinding is no alternative
     */
    fun getBase(binding: KeyBinding): KeyBinding? {
        return (binding as IKeyBinding).`tms$getParent`()
    }
}