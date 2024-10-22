package gg.aquatic.aquaticcrates.plugin.animation.pouch

import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.pouch.Pouch
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import org.bukkit.entity.Player
import java.util.*

class InstantPouchAnimationManager(
    override val pouch: Pouch,
    override val animationTasks: TreeMap<Int, MutableList<ConfiguredAction<PouchAnimation>>>,
    override val animationLength: Int,
    override val preAnimationDelay: Int,
    override val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<PouchAnimation>>>,
    override val postAnimationDelay: Int,
    override val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<PouchAnimation>>>,
    override val finalAnimationTasks: MutableList<ConfiguredAction<PouchAnimation>>,
    override val skippable: Boolean,
    override val openingBossbar: AnimationTitle,
) : PouchAnimationManager() {

    override val playingAnimations: HashMap<UUID, PouchAnimation> = hashMapOf()

    override fun skipAnimation(player: Player) {

    }

    override fun forceStopAnimation(player: Player) {

    }

    override fun tick() {
        for ((_, animation) in playingAnimations) {
            animation.tick()
        }
    }
}