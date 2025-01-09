package gg.aquatic.aquaticcrates.plugin.misc

import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.waves.util.Config
import gg.aquatic.waves.util.Message
import org.bukkit.configuration.file.FileConfiguration

enum class Messages(
    val path: String,
    val def: Any
) {

    HELP(
        "help", listOf(
            ""
        )
    ),
    PLUGIN_RELOADED("plugin-reloaded", "Plugin has been reloaded!"),

    // ERRORS
    NO_PERMISSION("no-permission", "You do not have permission to do this!"),
    UNKNOWN_CRATE("unknown-crate", "Unknown crate!"),
    UNKNOWN_NUMBER("unknown-number", "Unknown number format!"),
    UNKNOWN_PLAYER("unknown-player", "Unknown player!"),
    NO_KEY("no-key", "You do not have key to open this crate!")
    ;

    val message: Message
        get() {
            val value = cfg.get(path, def)
            return if (value is Collection<*>) {
                Message(value.map { it.toString() })
            } else
                Message(value.toString())
        }

    companion object {
        private val config = Config("messages.yml", CratesPlugin.INSTANCE)

        fun load() {
            config.load()
        }

        val cfg: FileConfiguration
            get() {
                return config.getConfiguration()!!
            }
    }
}