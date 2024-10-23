package gg.aquatic.aquaticcrates.plugin.animation.pouch

import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationSettings
import gg.aquatic.aquaticcrates.api.pouch.Pouch
import org.bukkit.entity.Player
import java.util.*

class PouchAnimationManagerImpl(
    override val pouch: Pouch,
    override val animationSettings: PouchAnimationSettings,
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