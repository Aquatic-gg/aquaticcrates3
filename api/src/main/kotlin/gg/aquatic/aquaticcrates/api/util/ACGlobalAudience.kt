package gg.aquatic.aquaticcrates.api.util

import gg.aquatic.waves.util.audience.AquaticAudience
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ACGlobalAudience: AquaticAudience {

    @Volatile
    var hidden = false

    var hiddenFrom = ConcurrentHashMap.newKeySet<Player>()

    override val uuids: Collection<UUID>
        get() {
            if (hidden) return emptyList()
            return Bukkit.getOnlinePlayers().mapNotNull { if (it in hiddenFrom) null else it.uniqueId }
        }

    override fun canBeApplied(player: Player): Boolean {
        if (hidden) return false
        if (player in hiddenFrom) return false
        return player.isOnline
    }
}