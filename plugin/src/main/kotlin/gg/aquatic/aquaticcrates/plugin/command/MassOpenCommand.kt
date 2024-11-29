package gg.aquatic.aquaticcrates.plugin.command

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticseries.lib.util.ICommand
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

object MassOpenCommand : ICommand {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission("aquaticcrates.admin")) {
            return
        }
        // massopen <player> <crate> <amount>
        if (args.size < 4) {
            sender.sendMessage("Usage: /acrates massopen <player> <crate> <amount> [threads]")
            return
        }
        val playerName = args[1]
        val player = Bukkit.getPlayer(playerName)
        if (player == null) {
            sender.sendMessage("Player $playerName not found")
            return
        }
        val crateName = args[2]
        val crate = CrateHandler.crates[crateName]
        if (crate == null) {
            sender.sendMessage("Crate $crateName not found")
            return
        }
        val amount = args[3].toIntOrNull()
        if (amount == null) {
            sender.sendMessage("Amount must be a number")
            return
        }

        var threadsAmount = 4
        if (args.size > 4) {
            threadsAmount = args[4].toIntOrNull() ?: 4

        }

        if (crate is OpenableCrate) {
            crate.massOpen(player, amount, threadsAmount)
        } else {
            sender.sendMessage("This crate cannot be mass opened!")
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
            2 -> {
                val list = ArrayList<String>()
                for (value in CrateHandler.crates.values) {
                    if (value is OpenableCrate) {
                        list += value.identifier
                    }
                }
                list
            }

            3 -> listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            4 -> listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            else -> listOf()
        }
    }
}