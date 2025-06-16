package dev.kingtux.tms


import dev.kingtux.tms.api.TMSKeyBinding

import dev.kingtux.tms.api.modifiers.BindingModifiers

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

import net.minecraft.util.Identifier
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

object TooManyShortcutsCore : ClientModInitializer {
    const val MOD_ID = "too_many_shortcuts"

    const val MOD_NAME = "Too Many Shortcuts"

    val LOGGER: Logger = LogManager.getLogger(TooManyShortcutsCore.javaClass)
    val currentModifiers: BindingModifiers = BindingModifiers()

    override fun onInitializeClient() {

    }



    fun log(level: Level?, message: String) {
        LOGGER.log(level, "[$MOD_ID]$message")
    }


    fun makeKeyID(keyName: String): String {
        return "key.$MOD_NAME.$keyName"
    }


}
