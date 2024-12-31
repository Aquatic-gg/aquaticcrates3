package gg.aquatic.aquaticcrates.plugin.command

import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.waves.command.ICommand
import org.bukkit.command.CommandSender

object ReloadCommand : ICommand{
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission("aquaticcrates.admin")) return
        sender.sendMessage("Reloading...")
        (CratesPlugin.INSTANCE as CratesPlugin).reloadPlugin().thenAccept {
            if (!it) {
                sender.sendMessage("Plugin is not fully loaded!")
                return@thenAccept
            }
            sender.sendMessage("Plugin has been reloaded!")
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return emptyList()
    }
}