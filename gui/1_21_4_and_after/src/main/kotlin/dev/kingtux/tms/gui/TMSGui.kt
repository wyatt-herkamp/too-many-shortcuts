package dev.kingtux.tms.gui

import net.fabricmc.api.ClientModInitializer
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object TMSGui : ClientModInitializer {
    const val MOD_ID = "tms-gui"
    val LOGGER: Logger = LogManager.getLogger(TMSGui.javaClass)

    const val MOD_NAME = "Too Many Shortcuts"
    override fun onInitializeClient() {

    }

    fun log(level: Level?, message: String) {
        LOGGER.log(level, "[$MOD_ID]$message")
    }

}