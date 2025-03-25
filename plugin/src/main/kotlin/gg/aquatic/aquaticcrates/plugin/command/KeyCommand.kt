package gg.aquatic.aquaticcrates.plugin.command

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.aquaticcrates.plugin.misc.Messages
import gg.aquatic.waves.command.ICommand
import gg.aquatic.waves.profile.toAquaticPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object KeyCommand : ICommand {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission("aquaticcrates.admin")) {
            Messages.NO_PERMISSION.message.send(sender)
            return
        }
        // acrates key give <crate> <player> <amount> [-s] [-off]
        // acrates key giveall <crate> <amount> [-s] [-off]
        // acrates key bank [player]

        if (args.size < 2) {
            Messages.HELP.send(sender)
            return
        }

        when (args[1].lowercase()) { // Start from args[1] to account for "key"
            "bank" -> {
                if (!sender.hasPermission("aquaticcrates.command.key.bank")) {
                    Messages.NO_PERMISSION.message.send(sender)
                    return
                }
                if (args.size == 3) {
                    if (!sender.hasPermission("aquaticcrates.command.key.bank.other")) {
                        Messages.NO_PERMISSION.message.send(sender)
                        return
                    }
                    val playerName = args[2]
                    val player = Bukkit.getPlayer(playerName)
                    if (player == null) {
                        Messages.UNKNOWN_PLAYER.message.send(sender)
                        return
                    }

                    val entry = player.toAquaticPlayer()?.crateEntry()
                    if (entry == null) {
                        Messages.NON_INITIALIZED_PLAYER.send(sender)
                        return
                    }

                    Messages.KEY_BANK_HEADER.send(sender)
                    for ((crateId, amount) in entry.balance) {
                        val key = Key.get(crateId) ?: continue
                        Messages.KEY_BANK_ENTRY.message.replace("%key%", key.crate.displayName)
                            .replace("%amount%", amount.toString()).send(sender)
                    }
                    Messages.KEY_BANK_FOOTER.send(sender)

                    return
                }

                if (sender !is Player) {
                    return
                }
                val entry = sender.toAquaticPlayer()?.crateEntry()
                if (entry == null) {
                    Messages.NON_INITIALIZED_PLAYER_SELF.send(sender)
                    return
                }

                Messages.KEY_BANK_HEADER.send(sender)
                for ((crateId, amount) in entry.balance) {
                    val key = Key.get(crateId) ?: continue
                    Messages.KEY_BANK_ENTRY.message.replace("%key%", key.crate.displayName)
                        .replace("%amount%", amount.toString()).send(sender)
                }
                Messages.KEY_BANK_FOOTER.send(sender)
            }

            "give" -> {
                if (args.size < 5) { // Ensure enough arguments for "give"
                    sender.sendMessage("Usage: /acrates key give <crate> <player> <amount> [-s] [-off] [-v]")
                    return
                }
                val crateId = args[2]
                val crate = CrateHandler.crates[crateId]
                if (crate == null) {
                    Messages.UNKNOWN_CRATE.message.send(sender)
                    return
                }
                val playerName = args[3]
                val amount = args[4].toIntOrNull() ?: run {
                    Messages.UNKNOWN_NUMBER.message.send(sender)
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
                    Messages.UNKNOWN_PLAYER.message.send(sender)
                    return
                }

                if (isVirtual) {
                    val aPlayer = player.toAquaticPlayer()?.crateEntry()
                    if (aPlayer == null) {
                        sender.sendMessage("Player is not initialized!")
                        return
                    }

                    aPlayer.give(amount, crateId)
                } else {
                    if (crate is OpenableCrate) {
                        crate.key.giveItem(amount, player)
                    }
                }
                if (!isSilent) {
                    Messages.KEY_RECEIVED.message.replace { str ->
                        str.replace("%amount%", amount.toString())
                            .replace("%id%", crateId)
                            .replace("%name%", crate.displayName)
                    }.send(player)
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
                    Messages.UNKNOWN_CRATE.message.send(sender)
                    return
                }
                val amount = args[3].toIntOrNull() ?: run {
                    Messages.UNKNOWN_NUMBER.message.send(sender)
                    return
                }

                // Handle optional flags
                val isSilent = args.contains("-s")
                val isOffline = args.contains("-off")
                val isVirtual = args.contains("-v")

                val given = hashSetOf<String>()
                for (player in Bukkit.getOnlinePlayers()) {
                    val aPlayer = player.toAquaticPlayer()?.crateEntry() ?: continue
                    given += player.name
                    if (isVirtual) {
                        aPlayer.give(amount, crateId)
                    } else {
                        if (crate is OpenableCrate) {
                            crate.key.giveItem(amount, player)
                        }
                    }
                    if (!isSilent) {
                        Messages.KEY_RECEIVED.message.replace { str ->
                            str.replace("%amount%", amount.toString())
                                .replace("%id%", crateId)
                                .replace("%name%", crate.displayName)
                        }.send(player)
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
                "giveall",
                "bank"
            )
        } else {
            when (args[0].lowercase()) {
                "give" -> {
                    if (!sender.hasPermission("aquaticcrates.admin")) return emptyList()
                    return when (args.size) {
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
                }

                "giveall" -> {
                    if (!sender.hasPermission("aquaticcrates.admin")) return emptyList()
                    return when (args.size) {
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

                "bank" -> {
                    if (args.size != 2) return emptyList()
                    if (!sender.hasPermission("aquaticcrates.command.key.bank.other")) {
                        return emptyList()
                    }
                    return Bukkit.getOnlinePlayers().map { it.name }
                }
            }
        }
        return listOf()
    }
}