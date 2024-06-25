package dev.kingtux.tms

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
object TooManyShortcuts : ClientModInitializer {
    const val MOD_ID = "too-many-shortcuts"
    const val MOD_NAME = "Too Many Shortcuts"
    val LOGGER: Logger = LogManager.getLogger("Too Many Shortcuts")
    override fun onInitializeClient() {
        LOGGER.info("Hello Fabric world!")
    }


}