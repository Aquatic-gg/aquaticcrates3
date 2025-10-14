package gg.aquatic.aquaticcrates.plugin.misc.hook

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.player.PlayerHandler
import gg.aquatic.aquaticcrates.plugin.log.toFriendlyTimeFromSeconds
import gg.aquatic.waves.util.PAPIUtil
import gg.aquatic.waves.util.chance.randomItem
import gg.aquatic.waves.util.toMMString
import org.bukkit.Bukkit

object PAPIHook {

    private val cachedPlaceholders = mutableMapOf<String, Pair<Long, String>>()

    internal fun registerPAPIHook() {
        PAPIUtil.registerExtension("Larkyy", "aquaticcrates") { offlinePlayer, str ->
            val args = str.split("_")
            if (args.isEmpty()) return@registerExtension ""

            when (args[0].lowercase()) {
                // %aquaticcrates_randomreward_<crate>_<time>_<only-winnable>%
                "randomreward" -> {
                    if (args.size < 4) return@registerExtension ""

                    val crateId = args[1]
                    val crate = CrateHandler.crates[crateId] ?: return@registerExtension ""
                    if (crate !is OpenableCrate) {
                        return@registerExtension ""
                    }
                    val time = args[2].toIntOrNull() ?: return@registerExtension ""
                    val onlyWinnable = args[3].toBooleanStrictOrNull() ?: return@registerExtension ""

                    val (previousTime, previousRewardName) = cachedPlaceholders.getOrPut(str) { System.currentTimeMillis() to "" }

                    val rewardName = if (previousTime + (time * 50) >= System.currentTimeMillis()) {
                        val newRewardName = (if (onlyWinnable) crate.rewardManager.getPossibleRewards(
                            offlinePlayer.player ?: return@registerExtension ""
                        ).values
                        else crate.rewardManager.rewards.values
                                ).randomItem()?.displayName ?: ""

                        cachedPlaceholders[str] = System.currentTimeMillis() to newRewardName
                        newRewardName
                    } else {
                        previousRewardName
                    }

                    return@registerExtension rewardName
                }
                // %aquaticcrates_latest-reward_<crate>_<place>_name%
                // %aquaticcrates_latest-reward_<crate>_<place>_id%
                // %aquaticcrates_latest-reward_<crate>_<place>_timestamp%
                // %aquaticcrates_latest-reward_<crate>_<place>_winner%
                "latest-reward" -> {
                    if (args.size < 4) return@registerExtension ""
                    val crateId = args[1]
                    val crate = CrateHandler.crates[crateId] ?: return@registerExtension ""
                    if (crate !is OpenableCrate) {
                        return@registerExtension ""
                    }
                    val place = args[2].toIntOrNull() ?: return@registerExtension ""
                    val found = HistoryHandler.latestRewards[crateId]?.getOrNull(place)

                    when (args[3].lowercase()) {
                        "name" -> {
                            return@registerExtension found?.reward?.displayName ?: ""
                        }

                        "id" -> {
                            return@registerExtension found?.reward?.id ?: ""
                        }

                        "timestamp" -> {
                            val timestamp = found?.timestamp ?: return@registerExtension ""
                            return@registerExtension timestamp.toFriendlyTimeFromSeconds()
                        }

                        "winner" -> {
                            return@registerExtension found?.winner ?: ""
                        }
                    }
                }

                // %aquaticcrates_guaranteed_<crate>_next_remaining%
                // %aquaticcrates_guaranteed_<crate>_next_required%
                // %aquaticcrates_guaranteed_<crate>_next_id%
                // %aquaticcrates_guaranteed_<crate>_next_name%
                "guaranteed" -> {
                    if (args.size < 4) return@registerExtension ""
                    val crateId = args[1]
                    val crate = CrateHandler.crates[crateId] ?: return@registerExtension ""
                    if (crate !is OpenableCrate) {
                        return@registerExtension ""
                    }

                    val arg2 = args[2].lowercase()
                    if (arg2 == "next") {
                        val arg3 = args[3].lowercase()

                        val guaranteedRewards = crate.rewardManager.guaranteedRewards
                        val player = offlinePlayer.player ?: return@registerExtension ""
                        val totalOpened = HistoryHandler.history(
                            crate.identifier,
                            CrateProfileEntry.HistoryType.ALLTIME,
                            player
                        )
                        val entry = guaranteedRewards.higherEntry(totalOpened)

                        when (arg3) {
                            "remaining" -> {
                                val entry = entry ?: return@registerExtension "0"
                                return@registerExtension (entry.key - totalOpened).toString()
                            }
                            "id" -> {
                                val entry = entry ?: return@registerExtension "none"
                                return@registerExtension entry.value.id
                            }
                            "required" -> {
                                val entry = entry ?: return@registerExtension "0"
                                return@registerExtension entry.key.toString()
                            }
                            "name" -> {
                                val entry = entry ?: return@registerExtension "0"
                                return@registerExtension entry.value.displayName
                            }
                        }
                    }
                    return@registerExtension ""
                }

                // %aquaticcrates_milestone_<crate>_<milestone>_reached%
                // %aquaticcrates_milestone_<crate>_<milestone>_name%
                "milestone" -> {
                    if (args.size < 4) return@registerExtension ""
                    val crateId = args[1]
                    val crate = CrateHandler.crates[crateId] ?: return@registerExtension ""
                    if (crate !is OpenableCrate) {
                        return@registerExtension ""
                    }
                    val milestoneId = args[2].toIntOrNull() ?: return@registerExtension ""
                    val milestones = crate.rewardManager.milestoneManager.milestones
                    val milestone = milestones[milestoneId] ?: return@registerExtension ""
                    return@registerExtension when (args[3].lowercase()) {
                        "remaining" -> {
                            crate.rewardManager.milestoneManager.remaining(
                                offlinePlayer.player ?: return@registerExtension "", milestoneId
                            ).toString()
                        }

                        "reached" -> {
                            val totalOpened = HistoryHandler.history(
                                crate.identifier,
                                CrateProfileEntry.HistoryType.ALLTIME,
                                offlinePlayer.player ?: return@registerExtension ""
                            )
                            if (totalOpened >= milestoneId) "yes" else "no"
                        }

                        "name" -> {
                            milestone.displayName.toMMString()
                        }

                        else -> ""
                    }
                }

                "repeatable-milestone" -> {
                    if (args.size < 4) return@registerExtension ""
                    val crateId = args[1]
                    val crate = CrateHandler.crates[crateId] ?: return@registerExtension ""
                    if (crate !is OpenableCrate) {
                        return@registerExtension ""
                    }
                    val milestoneId = args[2].toIntOrNull() ?: return@registerExtension ""
                    val milestones = crate.rewardManager.milestoneManager.repeatableMilestones
                    val milestone = milestones[milestoneId] ?: return@registerExtension ""
                    return@registerExtension when (args[3].lowercase()) {
                        "remaining" -> {
                            crate.rewardManager.milestoneManager.remainingRepeatable(
                                offlinePlayer.player ?: return@registerExtension "", milestoneId
                            ).toString()
                        }

                        "name" -> {
                            milestone.displayName.toMMString()
                        }

                        else -> ""
                    }
                }

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