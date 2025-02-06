package gg.aquatic.aquaticcrates.plugin.misc.hook

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.player.PlayerHandler
import gg.aquatic.waves.util.PAPIUtil
import org.bukkit.Bukkit

object PAPIHook {

    internal fun registerPAPIHook() {
        PAPIUtil.registerExtension("Larkyy", "aquaticcrates") { offlinePlayer, str ->
            val args = str.split("_")
            if (args.isEmpty()) return@registerExtension ""

            when (args[0].lowercase()) {
                "keys" -> {
                    if (args.size < 2) return@registerExtension ""
                    val player = offlinePlayer.player ?: return@registerExtension ""

                    val id = args.getOrNull(1) ?: return@registerExtension ""
                    return@registerExtension PlayerHandler.virtualKeys(player, id)?.toString() ?: ""
                }

                "totalkeys" -> {
                    if (args.size < 2) return@registerExtension ""
                    val player = offlinePlayer.player ?: return@registerExtension ""
                    val id = args.getOrNull(1) ?: return@registerExtension ""

                    return@registerExtension PlayerHandler.totalKeys(player, id)?.toString() ?: ""
                }
                // %aquaticcrates_statistic_crate_all_<alltime>%
                // %aquaticcrates_statistic_crate_<crateid>_<alltime>%
                // %aquaticcrates_statistic_crate_all_<alltime>_<player>%
                // %aquaticcrates_statistic_crate_all_<alltime>_self%
                // %aquaticcrates_statistic_crate_<crateid>_<alltime>_<player>%
                // %aquaticcrates_statistic_crate_<crateid>_<alltime>_self%

                // %aquaticcrates_statistic_reward_<crateid:rewardid>_<alltime>%
                // %aquaticcrates_statistic_reward_<crateid:rewardid>_<alltime>_player%
                // %aquaticcrates_statistic_reward_<crateid:rewardid>_<alltime>_self%
                "statistic" -> {
                    if (args.size < 2) return@registerExtension ""
                    if (args[1].lowercase() == "crate") {
                        if (args.size < 4) return@registerExtension ""
                        val crateId = args[2]

                        val crate = if (crateId.lowercase() == "all") {
                            null
                        } else {
                            CrateHandler.crates[crateId] ?: return@registerExtension ""
                        }

                        val timeframe =
                            CrateProfileEntry.HistoryType.valueOf((args.getOrNull(3) ?: "alltime").uppercase())
                        if (args.size >= 5) {
                            val playerName = args.subList(4, args.size).joinToString("_")
                            val player = if (playerName.lowercase() == "self") {
                                offlinePlayer.player ?: return@registerExtension ""
                            } else {
                                Bukkit.getPlayer(playerName) ?: return@registerExtension ""
                            }
                            if (crate != null) {
                                return@registerExtension HistoryHandler.history(crate.identifier, timeframe, player)
                                    .toString()
                            }
                            return@registerExtension HistoryHandler.history(timeframe, player).toString()
                        }

                        if (crate != null) {
                            return@registerExtension HistoryHandler.history(crate.identifier, timeframe).toString()
                        }
                        return@registerExtension HistoryHandler.history(timeframe).toString()
                    } else if (args[1].lowercase() == "reward") {
                        if (args.size < 4) return@registerExtension ""
                        val pairId = args[2].split(":")
                        if (pairId.size != 2) return@registerExtension ""
                        val crateId = pairId[0]
                        val rewardId = pairId[1]

                        val crate = CrateHandler.crates[crateId] ?: return@registerExtension ""

                        val timeframe =
                            CrateProfileEntry.HistoryType.valueOf((args.getOrNull(3) ?: "alltime").uppercase())
                        if (args.size >= 5) {
                            val playerName = buildString {
                                args.subList(4, args.size).forEach { append(it) }
                            }
                            val player = if (playerName.lowercase() == "self") {
                                offlinePlayer.player ?: return@registerExtension ""
                            } else {
                                Bukkit.getPlayer(playerName) ?: return@registerExtension ""
                            }
                            return@registerExtension HistoryHandler.rewardHistory(
                                crate.identifier,
                                rewardId,
                                timeframe,
                                player
                            ).toString()
                        }

                        return@registerExtension HistoryHandler.rewardHistory(crate.identifier, rewardId, timeframe)
                            .toString()
                    }
                }
            }
            return@registerExtension ""
        }
    }
}