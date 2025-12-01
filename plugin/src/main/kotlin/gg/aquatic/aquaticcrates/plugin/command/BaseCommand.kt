package gg.aquatic.aquaticcrates.plugin.command

import com.undefined.stellar.StellarCommand
import com.undefined.stellar.kotlin.*
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticcrates.plugin.log.LogMenu
import gg.aquatic.aquaticcrates.plugin.misc.Messages
import gg.aquatic.aquaticcrates.plugin.reward.menu.RewardsMenu
import gg.aquatic.waves.profile.toAquaticPlayer
import gg.aquatic.waves.util.task.AsyncCtx
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BaseCommand : KotlinBaseStellarCommand(
    "aquaticcrates", "", listOf(
        "acrates",
        "crates",
        "crate"
    )
) {
    override fun setup(): StellarCommand = kotlinCommand {
        execution<Player> {
            Messages.HELP.message.send(sender)
        }

        "help" {
            execution<CommandSender> {
                Messages.HELP.message.send(sender)
            }
        }

        "key" {
            literalArgument("bank") {
                requires("aquaticcrates.command.key.bank")
                allPlayersArgument("player", true) {
                    requires("aquaticcrates.command.key.bank.other")
                }
                runnable<CommandSender> {
                    val player = getOrNull<Player>("player")

                    if (player == null) {
                        if (sender !is Player) return@runnable false
                        val entry = (sender as Player).toAquaticPlayer()?.crateEntry()
                        if (entry == null) {
                            Messages.NON_INITIALIZED_PLAYER_SELF.send(sender)
                            return@runnable false
                        }

                        Messages.KEY_BANK_HEADER.send(sender)
                        for ((crateId, amount) in entry.balance) {
                            val key = Key.get(crateId) ?: continue
                            Messages.KEY_BANK_ENTRY.message.replace("%key%", key.crate.displayName)
                                .replace("%amount%", amount.toString()).send(sender)
                        }
                        Messages.KEY_BANK_FOOTER.send(sender)
                        return@runnable false
                    }

                    if (!sender.hasPermission("aquaticcrates.command.key.bank.other")) {
                        Messages.NO_PERMISSION.message.send(sender)
                        return@runnable false
                    }

                    val entry = player.toAquaticPlayer()?.crateEntry()
                    if (entry == null) {
                        Messages.NON_INITIALIZED_PLAYER.send(sender)
                        return@runnable false
                    }

                    Messages.KEY_BANK_HEADER.send(sender)
                    for ((crateId, amount) in entry.balance) {
                        val key = Key.get(crateId) ?: continue
                        Messages.KEY_BANK_ENTRY.message.replace("%key%", key.crate.displayName)
                            .replace("%amount%", amount.toString()).send(sender)
                    }
                    Messages.KEY_BANK_FOOTER.send(sender)

                    false
                }
            }

            literalArgument("give") {
                listArgument(
                    "crateId",
                    { CrateHandler.crates.keys.toList() },
                ) {
                    allPlayersArgument("player", true) {
                        asyncRunnable<CommandSender> {
                            val crateId: String by args
                            val player: Player by args

                            val crate = CrateHandler.crates[crateId]
                            if (crate == null) {
                                Messages.UNKNOWN_CRATE.message.send(sender)
                                return@asyncRunnable false
                            }

                            val amount = getOrNull<Int>("amount") ?: 1
                            val flags = getOrNull<String>("flags") ?: ""

                            val isSilent = flags.contains("-s")
                            val isVirtual = flags.contains("-v")

                            if (isVirtual) {
                                val aPlayer = player.toAquaticPlayer()?.crateEntry()
                                if (aPlayer == null) {
                                    sender.sendMessage("Player is not initialized!")
                                    return@asyncRunnable false
                                }

                                aPlayer.give(amount, crate.identifier)
                            } else {
                                if (crate is OpenableCrate) {
                                    (crate as OpenableCrate).key.giveItem(amount, player)
                                }
                            }
                            if (!isSilent) {
                                Messages.KEY_RECEIVED.message.replace { str ->
                                    str.replace("%amount%", amount.toString())
                                        .replace("%id%", crate.identifier)
                                        .replace("%name%", crate.displayName)
                                }.send(player)
                                sender.sendMessage("Key given!")
                            }
                            false
                        }
                        integerArgument("amount") {
                            phraseArgument("flags") {
                                addWordSuggestions(0, "-v", "-s")
                                addWordSuggestions(1, "-v", "-s")
                            }
                        }
                    }
                }

            }
            literalArgument("giveall") {
                listArgument(
                    "crateId",
                    { CrateHandler.crates.keys.toList() },
                ) {
                    asyncRunnable<CommandSender> {
                        val crateId: String by args
                        val amount = getOrNull<Int>("amount") ?: 1
                        val flags = getOrNull<String>("flags") ?: ""

                        val crate = CrateHandler.crates[crateId]
                        if (crate == null) {
                            Messages.UNKNOWN_CRATE.message.send(sender)
                            return@asyncRunnable false
                        }

                        val isSilent = flags.contains("-s")
                        val isVirtual = flags.contains("-v")

                        val given = hashSetOf<String>()
                        for (player in Bukkit.getOnlinePlayers()) {
                            val aPlayer = player.toAquaticPlayer()?.crateEntry() ?: continue
                            given += player.name
                            if (isVirtual) {
                                aPlayer.give(amount, crate.identifier)
                            } else {
                                if (crate is OpenableCrate) {
                                    (crate as OpenableCrate).key.giveItem(amount, player)
                                }
                            }
                            if (!isSilent) {
                                Messages.KEY_RECEIVED.message.replace { str ->
                                    str.replace("%amount%", amount.toString())
                                        .replace("%id%", crate.identifier)
                                        .replace("%name%", crate.displayName)
                                }.send(player)
                            }
                        }

                        if (!isSilent) {
                            sender.sendMessage("Key given to ${given.size} players!")
                        }
                        false
                    }
                    integerArgument("amount") {
                        phraseArgument("flags") {
                            addWordSuggestions(0, "-v", "-s")
                            addWordSuggestions(1, "-v", "-s")
                        }
                    }
                }
            }
        }


        "crate" {
            literalArgument("preview") {
                requires("aquaticcrates.admin")
                listArgument(
                    "crateId",
                    { CrateHandler.crates.keys.toList() }) {
                    allPlayersArgument("player", true) {
                        asyncExecution<CommandSender> {
                            val crateId: String by args
                            val player: Player by args

                            val crate = CrateHandler.crates[crateId]
                            if (crate == null) {
                                Messages.UNKNOWN_CRATE.message.send(sender)
                                return@asyncExecution
                            }

                            if (crate !is OpenableCrate) return@asyncExecution
                            crate.openPreview(player, null)
                        }
                    }
                }
            }
            literalArgument("give") {
                requires("aquaticcrates.admin")
                listArgument(
                    "crateId",
                    { CrateHandler.crates.keys.toList() }) {
                    asyncExecution<Player> {
                        val crateId: String by args

                        val crate = CrateHandler.crates[crateId]
                        if (crate == null) {
                            Messages.UNKNOWN_CRATE.message.send(sender)
                            return@asyncExecution
                        }
                        if (crate !is BasicCrate) return@asyncExecution
                        crate.crateItem.giveItem(sender)
                    }
                }
            }

            literalArgument("open") {
                requires("aquaticcrates.admin")
                listArgument(
                    "crateId",
                    { CrateHandler.crates.keys.toList() }) {
                    allPlayersArgument("player", true) {
                        phraseArgument("flags") {
                            addWordSuggestions(0, "-nokey", "-instant")
                            addWordSuggestions(1, "-nokey", "-instant")
                        }

                        runnable<CommandSender>(scope = AsyncCtx.scope) {
                            val crateId: String by args
                            val player: Player by args
                            val flags = getOrNull<String>("flags") ?: ""

                            val crate = CrateHandler.crates[crateId]
                            if (crate == null) {
                                Messages.UNKNOWN_CRATE.message.send(sender)
                                return@runnable false
                            }

                            val noKey = flags.contains("-nokey")
                            val isInstant = flags.contains("-instant")

                            if (crate !is BasicCrate) return@runnable false
                            if (isInstant) {
                                if (noKey) {
                                    crate.instantOpen(player, player.location, null)
                                    return@runnable false
                                }
                                crate.tryInstantOpen(player, player.location, null)
                                return@runnable false
                            }
                            if (noKey) {
                                crate.open(player, player.location, null)
                                return@runnable false
                            }
                            crate.tryOpen(player, player.location, null)

                            false
                        }
                    }
                }
            }

            literalArgument("massopen") {
                requires("aquaticcrates.admin")
                listArgument(
                    "crateId",
                    { CrateHandler.crates.keys.toList() }) {
                    allPlayersArgument("player", true) {
                        integerArgument("amount") {
                            phraseArgument("flags") {
                                addWordSuggestions(0, "-nokey")
                            }

                            runnable<CommandSender>(scope = AsyncCtx.scope) {
                                val player: Player by args
                                val crateId: String by args

                                val crate = CrateHandler.crates[crateId]
                                if (crate == null) {
                                    Messages.UNKNOWN_CRATE.message.send(sender)
                                    return@runnable false
                                }

                                val amount: Int by args
                                val flags = getOrNull<String>("flags") ?: ""
                                val noKey = flags.contains("-nokey")
                                if (crate !is OpenableCrate) return@runnable false

                                if (noKey) {
                                    crate.massOpen(player, amount)
                                    return@runnable false
                                }
                                crate.tryMassOpen(player, amount)

                                false
                            }
                        }
                    }
                }
            }
        }
        "log" {
            requires("aquaticcrates.admin")
            execution<Player> {
                val menu = LogMenu((CratesPlugin.getInstance() as CratesPlugin).logMenuSettings, sender)
                menu.open()
            }
        }
        "reload" {
            requires("aquaticcrates.admin")
            execution<CommandSender> {
                Messages.PLUGIN_RELOADING.send(sender)
                (CratesPlugin.getInstance() as CratesPlugin).reloadPlugin().thenAccept {
                    if (!it) {
                        Messages.PLUGIN_IS_NOT_LOADED.send(sender)
                        return@thenAccept
                    }
                    Messages.PLUGIN_RELOADED.send(sender)
                }
            }
        }
        "rewardmenu" {
            execution<Player> {
                val menu = RewardsMenu((CratesPlugin.getInstance() as CratesPlugin).rewardsMenuSettings, sender)
                menu.open()
            }
        }
    }
}