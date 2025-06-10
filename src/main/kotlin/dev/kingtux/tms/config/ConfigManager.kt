package dev.kingtux.tms.config

import dev.kingtux.tms.TooManyShortcuts.LOGGER
import dev.kingtux.tms.TooManyShortcuts.MOD_ID
import dev.kingtux.tms.alternatives.AlternativeKeyBinding
import dev.kingtux.tms.api.config.Config
import dev.kingtux.tms.api.config.ConfigBindings
import dev.kingtux.tms.api.config.ConfigKeyBinding
import dev.kingtux.tms.mlayout.IKeyBinding
import kotlinx.serialization.json.Json
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import java.nio.file.Path

class ConfigManager(private val configPath: Path) {
    var config: Config;

    companion object {
        // Singleton instance
        private var instance: ConfigManager? = null
        fun instance(): ConfigManager {
            if (instance == null) {
                load()
            }
            return instance!!
        }

        private fun configPath(): Path {
            return MinecraftClient.getInstance().runDirectory.toPath().resolve("${MOD_ID}.json")
        }

        private fun load() {
            instance = ConfigManager(configPath())
        }
    }

    init {
        if (!configPath.toFile().exists()) {
            // Create default config
            // TODO: Load from minecraft
            config = Config(
                scrollBindings = true,
                keybindings = mutableMapOf()
            )
            // Write default config to file
            val json = Json.encodeToString(config)
            configPath.toFile().writeText(json)
        } else {
            // Read file to string
            val fileContent = configPath.toFile().readText()
            // Parse string to Config object
            config = Json.decodeFromString<Config>(fileContent)
        }
    }

    fun saveConfig() {
        val json = Json.encodeToString(config)
        configPath.toFile().writeText(json)
    }

    fun saveBindings(allKeys: Array<KeyBinding>) {
        for (keyBinding in allKeys) {
            if (keyBinding is IKeyBinding) {
                if (keyBinding.`tms$isAlternative`()) {
                    continue
                }
                //LOGGER.info("Saving Keybinding: {}", keyBinding.translationKey)
                val alternatives = keyBinding.`tms$getAlternatives`();
                val trueAlternatives = mutableListOf<ConfigBindings>();
                if (alternatives != null) {
                    for (alternative in alternatives) {
                        if (alternative is IKeyBinding) {
                            trueAlternatives.add(alternative.`tms$toConfig`())
                        }
                    }
                }
                val bindings = ConfigKeyBinding(
                    (keyBinding as IKeyBinding).`tms$toConfig`(),
                    trueAlternatives
                )
                // LOGGER.debug("Keybinding {}", keyBinding)
                config.keybindings[keyBinding.translationKey] = bindings
            }
        }
        saveConfig()
    }

    fun loadBindings(allKeys: Array<KeyBinding>): Array<KeyBinding> {
        val newKeys = allKeys.toMutableList()
        for ((key, configBindings) in config.keybindings.entries) {
            //LOGGER.info("Loading Keybinding: {}",key)
            val keyBinding = newKeys.find {
                it.translationKey == key
            } as IKeyBinding?

            if (keyBinding == null) {
                LOGGER.error("Keybinding not found: {}", key)
                continue
            }
            keyBinding.`tms$fromConfig`(configBindings.primaryBinding)

            if (configBindings.hasAlternatives()) {
                // LOGGER.info("Loading Alternatives for: {}",key)
                for (alternative in configBindings.alternatives) {
                    newKeys.add(AlternativeKeyBinding(keyBinding as KeyBinding, alternative))
                }
            }
        }
        return newKeys.toTypedArray()
    }

}