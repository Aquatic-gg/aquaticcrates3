package gg.aquatic.aquaticcrates.plugin.animation.crate

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.animation.SpawnedRewardVisual
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class CrateAnimationManagerImpl(
    override val crate: OpenableCrate,
    override val animationSettings: CrateAnimationSettings,
    rerollManager: (OpenableCrate) -> RerollManager,
) : CrateAnimationManager() {
    override val rerollManager = rerollManager(crate)
    override val playingAnimations: HashMap<UUID, MutableList<CrateAnimation>> = hashMapOf()

    override fun tick() {
        for ((_, animations) in playingAnimations) {
            for (animation in animations) {
                animation.tick()
            }
        }
    }

    override fun skipAnimation(player: Player) {

    }

    override fun forceStopAnimation(player: Player) {

    }
}