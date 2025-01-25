package gg.aquatic.aquaticcrates.plugin.command

import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.log.LogMenu
import gg.aquatic.waves.command.ICommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object LogCommand: ICommand {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (sender !is Player) return
        if (!sender.hasPermission("aquaticcrates.admin")) return

        val menu = LogMenu((CratesPlugin.INSTANCE as CratesPlugin).logMenuSettings, sender)
        menu.open()
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return emptyList()
    }
}