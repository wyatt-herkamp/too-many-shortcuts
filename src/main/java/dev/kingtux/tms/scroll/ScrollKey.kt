package dev.kingtux.tms.scroll

import dev.kingtux.tms.TooManyShortcuts
import dev.kingtux.tms.TooManyShortcuts.log
import net.minecraft.client.util.InputUtil
import org.apache.logging.log4j.Level

// TODO Handle Left and Right Scrolling
enum class ScrollKey(val id: Int) {
    UP(512),
    DOWN(513);

    /// Returns the translation key for the scroll key
    /// EXAMPLE: key.too_many_shortcuts.mouse.scroll.down
    fun translationKey(): String {
        return "key.${TooManyShortcuts.MOD_ID}.mouse.scroll.${name.lowercase()}";
    }
    /// Returns the translation text for the scroll key
    fun inputKey(): InputUtil.Key{
        return InputUtil.Type.MOUSE.createFromCode(id)
    }
    /// Returns the key id for the scroll key
    fun inputKeyID(): String{
        return "mouse.scroll.${name.lowercase()}"
    }
    companion object{
        private var HAS_REGISTERED = false
        /// Returns the ScrollKey from the id
        fun fromId(id: Int): ScrollKey? {
            return entries.find { it.id == id }
        }
        /// If the deltaY is greater than 0 return UP else return DOWN
        fun getVerticalKey(deltaY: Double): ScrollKey {
            return if (deltaY > 0) UP else DOWN
        }
        fun registerMouseScrollKeys(){
            if(HAS_REGISTERED) {
                log(Level.WARN, "Scroll keys have already been registered!")
                return
            }
            for (entry in ScrollKey.entries) {
                log(Level.DEBUG, "Registering key: ${entry.translationKey()} with id: ${entry.id}")
                InputUtil.Type.mapKey(InputUtil.Type.MOUSE, entry.translationKey(), entry.id)
                InputUtil.Key.KEYS[entry.translationKey()] = InputUtil.fromTranslationKey(entry.translationKey())
            }
            HAS_REGISTERED = true
        }
    }

}