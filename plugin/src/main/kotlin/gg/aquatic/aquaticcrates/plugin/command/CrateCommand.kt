package gg.aquatic.aquaticcrates.plugin.command

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticcrates.plugin.editor.category.MainEditorCategory
import gg.aquatic.aquaticcrates.plugin.editor.data.CrateModel
import gg.aquatic.aquaticcrates.plugin.editor.menu.EditorMenu
import gg.aquatic.aquaticcrates.plugin.misc.Messages
import gg.aquatic.waves.command.ICommand
import gg.aquatic.waves.util.task.AsyncCtx
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object CrateCommand : ICommand {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission("aquaticcrates.admin")) {
            Messages.NO_PERMISSION.message.send(sender)
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
            "edit" -> {
                if (!sender.hasPermission("aquaticcrates.admin")) return
                if (args.size < 3) {
                    sender.sendMessage("Usage: /acrates crate edit <crate>")
                    return
                }
                if (sender !is Player) {
                    sender.sendMessage("You must be a player to use this command!")
                    return
                }
                val crateName = args[2]
                val crate = CrateHandler.crates[crateName]
                if (crate == null) {
                    Messages.UNKNOWN_CRATE.message.send(sender)
                    return
                }
                sender.sendMessage("Opening editor...")
                val dataModel = CrateModel.of(crate as BasicCrate)
                val menu = EditorMenu(dataModel, sender, MainEditorCategory(dataModel), null)
                menu.open()
            }

            "preview" -> {
                if (args.size < 4) {
                    sender.sendMessage("Usage: /acrates crate preview <crate> <player>")
                    return
                }
                val crateName = args[2]
                val crate = CrateHandler.crates[crateName]
                if (crate == null) {
                    Messages.UNKNOWN_CRATE.message.send(sender)
                    return
                }
                if (crate !is BasicCrate) return
                val playerName = args[3]
                val player = sender.server.getPlayer(playerName)
                if (player == null) {
                    Messages.UNKNOWN_PLAYER.message.send(sender)
                    return
                }

                crate.openPreview(player, null)
            }

            "give" -> {
                if (sender !is Player) {
                    return
                }
                if (args.size < 3) {
                    sender.sendMessage("Usage: /acrates crate give <crate>")
                    return
                }
                val crateName = args[2]
                val crate = CrateHandler.crates[crateName]
                if (crate == null) {
                    Messages.UNKNOWN_CRATE.message.send(sender)
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
                    Messages.UNKNOWN_CRATE.message.send(sender)
                    return
                }
                if (crate !is BasicCrate) return
                val playerName = args[3]
                val player = sender.server.getPlayer(playerName)
                if (player == null) {
                    Messages.UNKNOWN_PLAYER.message.send(sender)
                    return
                }
                val noKey = args.contains("-nokey")
                val isInstant = args.contains("-instant")

                AsyncCtx {
                    if (isInstant) {
                        if (noKey) {
                            crate.instantOpen(player, player.location, null)
                            return@AsyncCtx
                        }
                        crate.tryInstantOpen(player, player.location, null)
                        return@AsyncCtx
                    }
                    if (noKey) {
                        crate.open(player, player.location, null)
                        return@AsyncCtx
                    }
                    crate.tryOpen(player, player.location, null)
                }

            }

            "massopen" -> {
                if (args.size < 5) {
                    sender.sendMessage("Usage: /acrates crate massopen <crate> <player> <amount> [-nokey]")
                    return
                }
                val playerName = args[3]
                val player = Bukkit.getPlayer(playerName)
                if (player == null) {
                    Messages.UNKNOWN_PLAYER.message.send(sender)
                    return
                }
                val crateName = args[2]
                val crate = CrateHandler.crates[crateName]
                if (crate == null) {
                    Messages.UNKNOWN_CRATE.message.send(sender)
                    return
                }
                val amount = args[4].toIntOrNull()
                if (amount == null) {
                    Messages.UNKNOWN_NUMBER.message.send(sender)
                    return
                }

                val noKey = args.contains("-nokey")

                AsyncCtx {
                    if (crate is OpenableCrate) {
                        if (noKey) {
                            crate.massOpen(player, amount)
                        } else {
                            crate.tryMassOpen(player, amount)
                        }
                    } else {
                        sender.sendMessage("This crate cannot be mass opened!")
                    }
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
                "massopen",
                "preview",
                "edit"
            )
        }
        return when (args[0].lowercase()) {
            "edit" -> {
                if (args.size == 2) {
                    CrateHandler.crates.keys.toList()
                } else {
                    listOf()
                }
            }
            "give" -> {
                if (args.size == 2) {
                    CrateHandler.crates.keys.toList()
                } else {
                    listOf()
                }
            }

            "preview" -> {
                if (args.size == 2) {
                    CrateHandler.crates.keys.toList()
                } else {
                    Bukkit.getOnlinePlayers().map { it.name }
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