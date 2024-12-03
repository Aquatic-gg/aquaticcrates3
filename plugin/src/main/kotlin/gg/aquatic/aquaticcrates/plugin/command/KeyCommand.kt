package gg.aquatic.aquaticcrates.plugin.command

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.aquaticseries.lib.util.ICommand
import gg.aquatic.waves.profile.toAquaticPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

object KeyCommand : ICommand {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission("aquaticcrates.admin")) {
            return
        }
        // acrates key give <crate> <player> <amount> [-s] [-off]
        // acrates key giveall <crate> <amount> [-s] [-off]

        if (args.size < 2) {
            sender.sendMessage("Invalid command usage. Please specify a subcommand.")
            return
        }

        when (args[1].lowercase()) { // Start from args[1] to account for "key"
            "give" -> {
                if (args.size < 5) { // Ensure enough arguments for "give"
                    sender.sendMessage("Usage: /acrates key give <crate> <player> <amount> [-s] [-off] [-v]")
                    return
                }
                val crateId = args[2]
                val crate = CrateHandler.crates[crateId]
                if (crate == null) {
                    sender.sendMessage("Crate $crateId not found")
                    return
                }
                val playerName = args[3]
                val amount = args[4].toIntOrNull() ?: run {
                    sender.sendMessage("Invalid amount specified.")
                    return
                }

                // Handle optional flags
                val isSilent = args.contains("-s")
                val isOffline = args.contains("-off")
                val isVirtual = args.contains("-v")

                val player = Bukkit.getPlayer(playerName)
                if (player == null) {
                    if (isOffline) {
                        sender.sendMessage("Trying to give the key to offline player...")
                        return
                    }
                    sender.sendMessage("Player $playerName not found")
                    return
                }

                val aPlayer = player.toAquaticPlayer()?.crateEntry()
                if (aPlayer == null) {
                    sender.sendMessage("Player is not initialized!")
                    return
                }

                if (isVirtual) {
                    aPlayer.give(amount, crateId)
                    return
                } else {
                    if (crate is OpenableCrate) {
                        crate.key.giveItem(amount, player)
                    }
                }
                if (!isSilent) {
                    player.sendMessage("You have received key!")
                    sender.sendMessage("Key given!")
                }
            }

            "giveall" -> {
                if (args.size < 4) { // Ensure enough arguments for "giveall"
                    sender.sendMessage("Usage: /acrates key giveall <crate> <amount> [-s] [-off]")
                    return
                }
                val crateId = args[2]
                val crate = CrateHandler.crates[crateId]
                if (crate == null) {
                    sender.sendMessage("Crate $crateId not found")
                    return
                }
                val amount = args[3].toIntOrNull() ?: run {
                    sender.sendMessage("Invalid amount specified.")
                    return
                }

                // Handle optional flags
                val isSilent = args.contains("-s")
                val isOffline = args.contains("-off")
                val isVirtual = args.contains("-v")

                val given = hashSetOf<String>()
                for (player in Bukkit.getOnlinePlayers()) {
                    val aPlayer = player.toAquaticPlayer()?.crateEntry()
                    if (aPlayer == null) {
                        continue
                    }
                    given += player.name
                    if (isVirtual) {
                        aPlayer.give(amount, crateId)
                    } else {
                        if (crate is OpenableCrate) {
                            crate.key.giveItem(amount, player)
                        }
                    }
                    if (!isSilent) {
                        player.sendMessage("You have received key!")
                    }
                }

                if (isOffline) {
                    if (!isVirtual) {
                        sender.sendMessage("Keys are gonna be given as virtual keys to offline players!")
                    }
                    sender.sendMessage("Trying to give the key to offline players...")
                    // TODO: Offline player give
                }

                if (!isSilent) {
                    sender.sendMessage("Key given to ${given.size} players!")
                }
            }

            else -> {
                sender.sendMessage("Unknown subcommand. Usage: /acrates key <give|giveall> ...")
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
                "giveall"
            )
        } else {
            if (args[0].equals("give", ignoreCase = true)) {
                return when(args.size) {
                    2 -> {
                        CrateHandler.crates.keys.toList()
                    }
                    3 -> {
                        Bukkit.getOnlinePlayers().map { it.name }
                    }
                    4 -> {
                        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                    }
                    else -> {
                        listOf(
                            "-s",
                            "-off",
                            "-v"
                        )
                    }
                }
            } else if (args[0].equals("giveall", ignoreCase = true)) {
                return when(args.size) {
                    2 -> {
                        CrateHandler.crates.keys.toList()
                    }

                    3 -> {
                        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                    }

                    else -> {
                        listOf(
                            "-s",
                            "-off",
                            "-v"
                        )
                    }
                }
            }
        }
        return listOf()
    }
}