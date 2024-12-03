package gg.aquatic.aquaticcrates.plugin.command

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticseries.lib.util.ICommand
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CrateCommand : ICommand {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission("aquaticcrates.admin")) {
            return
        }
        if (sender !is Player) {
            return
        }
        // acrates crate give <crate>
        // acrates crate open <crate> <player> -nokey -instant
        // acrates crate massopen <crate> <player> <amount> [threads] -nokey
        if (args.size < 2) {
            sender.sendMessage("Usage: /acrates crate give/open/massopen ...")
            return
        }
        when (args[1].lowercase()) {
            "give" -> {
                if (args.size < 3) {
                    sender.sendMessage("Usage: /acrates crate give <crate>")
                    return
                }
                val crateName = args[2]
                val crate = CrateHandler.crates[crateName]
                if (crate == null) {
                    sender.sendMessage("Crate $crateName not found")
                    return
                }

                if (crate !is BasicCrate) return
                crate.crateItem.giveItem(sender)
            }

            "open" -> {
                if (args.size < 4) {
                    sender.sendMessage("Usage: /acrates crate open <crate> <player> [-nokey] [-instant]")
                    return
                }
                val crateName = args[2]
                val crate = CrateHandler.crates[crateName]
                if (crate == null) {
                    sender.sendMessage("Crate $crateName not found")
                    return
                }
                if (crate !is BasicCrate) return
                val playerName = args[3]
                val player = sender.server.getPlayer(playerName)
                if (player == null) {
                    sender.sendMessage("Player $playerName not found")
                    return
                }
                val noKey = args.contains("-nokey")
                val isInstant = args.contains("-instant")

                if (isInstant) {
                    if (noKey) {
                        crate.instantOpen(player, player.location, null)
                        return
                    }
                    crate.tryInstantOpen(player, player.location, null)
                    return
                }
                if (noKey) {
                    crate.open(player, player.location, null)
                    return
                }
                crate.tryOpen(player, player.location, null)
            }

            "massopen" -> {
                if (args.size < 5) {
                    sender.sendMessage("Usage: /acrates crate massopen <crate> <player> <amount> [threads] [-nokey]")
                    return
                }
                val playerName = args[3]
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
                val amount = args[4].toIntOrNull()
                if (amount == null) {
                    sender.sendMessage("Amount must be a number")
                    return
                }

                var noKey = args.contains("-nokey")
                var threadsAmount = 4
                if (args.size > 5) {
                    threadsAmount = args[5].toIntOrNull() ?: 4
                }

                if (crate is OpenableCrate) {
                    if (noKey) {
                        crate.massOpen(player, amount, threadsAmount)
                    } else {
                        crate.tryMassOpen(player, amount, threadsAmount)
                    }
                } else {
                    sender.sendMessage("This crate cannot be mass opened!")
                }
            }
        }


    }

    override fun tabComplete(
        sender: CommandSender,
        args: Array<out String>
    ): List<String> {
        if (args.size == 1) {
            return listOf(
                "give",
                "open",
                "massopen"
            )
        }
        return when (args[0].lowercase()) {
            "give" -> {
                if (args.size == 2) {
                    CrateHandler.crates.keys.toList()
                } else {
                    listOf()
                }
            }

            "open" -> {
                when (args.size) {
                    2 -> {
                        CrateHandler.crates.keys.toList()
                    }

                    3 -> {
                        Bukkit.getOnlinePlayers().map { it.name }
                    }

                    else -> {
                        listOf("-nokey", "-instant")
                    }
                }
            }

            "massopen" -> {
                when (args.size) {
                    2 -> {
                        CrateHandler.crates.keys.toList()
                    }

                    3 -> {
                        Bukkit.getOnlinePlayers().map { it.name }
                    }


                    4 -> {
                        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                    }

                    5 -> {
                        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                    }

                    else -> {
                        listOf("-nokey")
                    }
                }
            }

            else -> {
                listOf()
            }
        }
    }
}