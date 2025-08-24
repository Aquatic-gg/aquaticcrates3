package gg.aquatic.aquaticcrates.plugin.command

import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.misc.Messages
import gg.aquatic.waves.command.ICommand
import org.bukkit.command.CommandSender

object ReloadCommand : ICommand{
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission("aquaticcrates.admin")) {
            Messages.NO_PERMISSION.message.send(sender)
            return
        }
        Messages.PLUGIN_RELOADING.send(sender)
        (CratesPlugin.getInstance() as CratesPlugin).reloadPlugin().thenAccept {
            if (!it) {
                Messages.PLUGIN_IS_NOT_LOADED.send(sender)
                return@thenAccept
            }
            Messages.PLUGIN_RELOADED.send(sender)
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return emptyList()
    }
}