package gg.aquatic.aquaticcrates.plugin.misc.hook

import com.hibiscusmc.hmccosmetics.api.HMCCosmeticsAPI
import com.hibiscusmc.hmccosmetics.user.CosmeticUser
import gg.aquatic.aquaticcrates.api.event.CrateAnimationEndEvent
import gg.aquatic.aquaticcrates.api.event.CrateAnimationStartEvent
import gg.aquatic.aquaticcrates.plugin.animation.open.CinematicAnimationImpl
import gg.aquatic.waves.api.event.event
import gg.aquatic.waves.util.task.BukkitCtx
import kotlinx.coroutines.launch

class HMCCosmeticsHook {

    init {
        event<CrateAnimationStartEvent> { e ->
            val player = e.player
            if (e.animation !is CinematicAnimationImpl) return@event
            val user = HMCCosmeticsAPI.getUser(player.uniqueId) ?: return@event
            BukkitCtx {
                user.hideCosmetics(CosmeticUser.HiddenReason.PLUGIN)
            }
        }
        event<CrateAnimationEndEvent> { e ->
            val player = e.player
            if (e.animation !is CinematicAnimationImpl) return@event
            val user = HMCCosmeticsAPI.getUser(player.uniqueId) ?: return@event
            BukkitCtx {
                user.showCosmetics(CosmeticUser.HiddenReason.PLUGIN)
            }
        }
    }

}