package gg.aquatic.aquaticcrates.plugin.misc

import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.waves.util.Config
import gg.aquatic.waves.util.message.handler.CfgMessageHandler
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration

enum class Messages(
    override val path: String,
    override val def: Any
): CfgMessageHandler {

    HELP(
        "help", listOf(
            ""
        )
    ),
    PLUGIN_RELOADED("plugin-reloaded", "Plugin has been reloaded!"),
    PLUGIN_RELOADING("plugin-reloading", "Plugin is being reloaded!"),

    // ERRORS
    NO_PERMISSION("no-permission", "You do not have permission to do this!"),
    UNKNOWN_CRATE("unknown-crate", "Unknown crate!"),
    UNKNOWN_NUMBER("unknown-number", "Unknown number format!"),
    UNKNOWN_PLAYER("unknown-player", "Unknown player!"),
    KEY_RECEIVED("key-received", "&fYou have received &7x%amount% %id% &fkey!"),
    PLUGIN_IS_NOT_LOADED("plugin-is-not-loaded", "Plugin is not loaded!"),
    KEY_BANK_HEADER("key-bank.header", "Your virtual keys: "),
    KEY_BANK_ENTRY("key-bank.entry", "&7- &f%key%: %amount%"),
    KEY_BANK_FOOTER("key-bank.footer", ""),
    NON_INITIALIZED_PLAYER("non-initialized-player", "Player is not initialized!"),
    NON_INITIALIZED_PLAYER_SELF("non-initialized-player-self", "Your profile is not initialized!"),
    ;

    override val config: FileConfiguration
        get() = cfg

    fun send(sender: CommandSender) = message.send(sender)

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