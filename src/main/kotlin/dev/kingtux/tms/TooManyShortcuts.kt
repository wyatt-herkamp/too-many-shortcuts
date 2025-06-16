package dev.kingtux.tms


import net.fabricmc.api.ClientModInitializer


import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object TooManyShortcuts : ClientModInitializer {
    const val MOD_ID = "too_many_shortcuts"

    const val MOD_NAME = "Too Many Shortcuts"

    val LOGGER: Logger = LogManager.getLogger(TooManyShortcuts.javaClass)



    override fun onInitializeClient() {

    }


    fun log(level: Level?, message: String) {
        LOGGER.log(level, "[$MOD_ID]$message")
    }



    fun makeKeyID(keyName: String): String {
        return "key.$MOD_NAME.$keyName"
    }

}
