package gg.aquatic.aquaticcrates.plugin.animation.open.settings

import gg.aquatic.aquaticcrates.api.animation.crate.*
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.open.RegularAnimationImpl
import gg.aquatic.aquaticcrates.api.util.ACGlobalAudience
import gg.aquatic.waves.util.audience.FilterAudience
import gg.aquatic.waves.util.audience.GlobalAudience
import gg.aquatic.waves.util.runLaterSync
import gg.aquatic.waves.util.runSync
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class RegularAnimationSettings(
    override val animationTasks: TreeMap<Int, CrateAnimationActions>,
    override val animationLength: Int,
    override val preAnimationDelay: Int,
    override val preAnimationTasks: TreeMap<Int,CrateAnimationActions>,
    override val postAnimationDelay: Int,
    override val postAnimationTasks: TreeMap<Int, CrateAnimationActions>,
    override val finalAnimationTasks: CrateAnimationActions,
    override val skippable: Boolean,
    val personal: Boolean,
) : CrateAnimationSettings() {

    override fun create(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location,
        rolledRewards: MutableList<RolledReward>
    ): CompletableFuture<Void> {
        val animation = RegularAnimationImpl(
            player,
            animationManager,
            location,
            rolledRewards,
            if (personal) FilterAudience { it == player } else GlobalAudience(),
            CompletableFuture()
        )

        val spawnedCrate = CrateHandler.spawned[location]
        animation.tick()
        runLaterSync(1) {
            if (!personal) {
                spawnedCrate?.spawnedInteractables?.forEach {
                    val audience = it.audience
                    if (audience is ACGlobalAudience) {
                        audience.hidden = true
                    }
                    for (uuid in audience.uuids) {
                        val op = Bukkit.getPlayer(uuid)
                        if (op != null) {
                            it.removeViewer(op)
                        }
                    }
                }
            } else {
                spawnedCrate?.spawnedInteractables?.forEach { it.removeViewer(player) }
            }
        }

        animationManager.playAnimation(animation)
        return animation.completionFuture.thenRun {
            runSync {
                if (!personal) {
                    if (spawnedCrate != null) {
                        for (spawnedInteractable in spawnedCrate.spawnedInteractables) {
                            val audience = spawnedInteractable.audience
                            if (audience is ACGlobalAudience) {
                                audience.hidden = false

                                spawnedInteractable.updateViewers()
                                continue
                            }
                            spawnedInteractable.updateViewers()
                        }
                    }
                } else {
                    spawnedCrate?.spawnedInteractables?.forEach { it.addViewer(player) }
                }
            }
        }
    }

    override fun canBeOpened(player: Player, animationManager: CrateAnimationManager, location: Location): AnimationResult {
        if (animationManager.playingAnimations.isNotEmpty() && !personal) return AnimationResult.ALREADY_BEING_OPENED_OTHER
        if (personal && animationManager.playingAnimations.containsKey(player.uniqueId)) return AnimationResult.ALREADY_BEING_OPENED
        return AnimationResult.SUCCESS
    }

    companion object: AnimationSettingsFactory() {
        override fun serialize(section: ConfigurationSection?): CrateAnimationSettings? {
            if (section == null) return null
            return RegularAnimationSettings(
                loadAnimationTasks(section.getConfigurationSection("tasks")),
                loadAnimationLength(section),
                loadPreAnimationDelay(section),
                loadPreAnimationTasks(section),
                loadPostAnimationDelay(section),
                loadPostAnimationTasks(section),
                loadFinalActions(section),
                loadSkippable(section),
                loadIsPersonal(section)
            )
        }

    }
}