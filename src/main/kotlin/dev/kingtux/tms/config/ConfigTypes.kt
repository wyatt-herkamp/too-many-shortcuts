package dev.kingtux.tms.config
import dev.kingtux.tms.api.modifiers.BindingModifiers
import kotlinx.serialization.*
@Serializable
data class Config(
    val scrollBindings: Boolean = true,
    val keybindings: MutableMap<String, ConfigKeyBinding>,
)
@Serializable
data class ConfigKeyBinding(
    var primaryBinding: ConfigBindings,
    var alternatives: MutableList<ConfigBindings> = mutableListOf(),
){
    constructor(primaryBinding: ConfigBindings): this( primaryBinding, mutableListOf())
    fun doesPrimaryHaveModifiers(): Boolean {
        return primaryBinding.hasModifiers()
    }
    fun hasAlternatives(): Boolean {
        return alternatives.isNotEmpty()
    }
}
@Serializable
data class ConfigBindings(
    val key: String,
    val modifiers: BindingModifiers = BindingModifiers()
){

    fun hasModifiers(): Boolean {
        return modifiers.shift || modifiers.ctrl || modifiers.alt
    }
}
