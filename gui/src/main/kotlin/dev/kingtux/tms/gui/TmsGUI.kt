package dev.kingtux.tms.gui

import dev.kingtux.tms.TooManyShortcutsCore
import dev.kingtux.tms.TooManyShortcutsCore.MOD_ID
import net.fabricmc.api.ClientModInitializer
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object TmsGUI : ClientModInitializer {
    const val MOD_ID = "tms_gui"

    const val MOD_NAME = "Too Many Shortcuts GUI"
    val LOGGER: Logger = LogManager.getLogger(TmsGUI.javaClass)
    override fun onInitializeClient() {

    }
    fun log(level: Level?, message: String) {
        LOGGER.log(level, "[$MOD_ID]$message")
    }
}