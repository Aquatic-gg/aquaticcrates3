package gg.aquatic.aquaticcrates.api.util

import gg.aquatic.waves.util.audience.AquaticAudience
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class ACGlobalAudience: AquaticAudience {

    var hidden = false

    override val uuids: Collection<UUID>
        get() {
            if (hidden) return emptyList()
            return Bukkit.getOnlinePlayers().map { it.uniqueId }
        }

    override fun canBeApplied(player: Player): Boolean {
        if (hidden) return false
        return player.isOnline
    }
}