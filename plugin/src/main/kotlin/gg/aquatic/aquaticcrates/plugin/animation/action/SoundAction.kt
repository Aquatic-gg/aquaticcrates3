package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import org.bukkit.Bukkit

class SoundAction : Action<Animation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("sound","",true),
        PrimitiveObjectArgument("pitch",1f,false),
        PrimitiveObjectArgument("volume",100f,false)
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val sound = args.string("sound") { textUpdater(binder, it)} ?: return
        val pitch = args.float("pitch") { textUpdater(binder, it)} ?: return
        val volume = args.float("volume") { textUpdater(binder, it)} ?: return

        for (uuid in binder.audience.uuids) {
            val player = Bukkit.getPlayer(uuid) ?: continue
            if (player.location.world != binder.baseLocation.world) continue
            if (player.location.distanceSquared(binder.baseLocation) > 2500) continue
            player.playSound(
                binder.baseLocation,
                sound,
                volume,
                pitch
            )
        }
        //binder.player.playSound(binder.player.location, sound, volume, pitch)
    }
}