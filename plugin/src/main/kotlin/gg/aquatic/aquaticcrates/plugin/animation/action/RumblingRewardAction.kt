package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.plugin.animation.prop.RumblingRewardProp
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.ActionsArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.decimals
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.generic.ClassTransform
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import net.kyori.adventure.key.Key
import org.bukkit.entity.Player

@RegisterAction("rumbling-reward")
class RumblingRewardAction : Action<CrateAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("rumbling-length", 0, false),
        PrimitiveObjectArgument("rumbling-period", 0, false),
        PrimitiveObjectArgument("ease-out", false, required = false),
        PrimitiveObjectArgument("reward-index", 0, false),
        ActionsArgument("rumble-actions", null, false, CrateAnimation::class.java, listOf(
            ClassTransform(Player::class.java) { a -> a.player }
        )),
        ActionsArgument("rumble-finish-actions", null, false, CrateAnimation::class.java, listOf(
            ClassTransform(Player::class.java) { a -> a.player }
        )),
    )

    override fun execute(binder: CrateAnimation, args: ObjectArguments, textUpdater: (CrateAnimation, String) -> String) {
        val id = args.string("id") { textUpdater(binder, it) } ?: "example"
        val rumblingLength = args.int("rumbling-length") { textUpdater(binder, it) } ?: return
        val rumblingPeriod = args.int("rumbling-period") { textUpdater(binder, it) } ?: return
        val easeOut = args.boolean("ease-out") { textUpdater(binder, it) } ?: false
        val rewardIndex = args.int("reward-index") {textUpdater(binder,it)} ?: 0
        val rumbleActions = args.typed<Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>>("rumble-actions") ?: listOf()
        val rumbleFinishActions = args.typed<Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>>("rumble-finish-actions") ?: listOf()

        val prop = RumblingRewardProp(
            binder,
            rumblingLength,
            rumblingPeriod,
            easeOut,
            rewardIndex,
            rumbleActions,
            rumbleFinishActions
        )

        binder.extraPlaceholders[Key.key("rumbling-reward-name:$id")] = { str: String ->
            str.replace(
                "%rumbling-reward-name:$id%",
                prop.currentReward?.displayName ?: ""
            )
        }
        binder.extraPlaceholders[Key.key("rumbling-reward-id:$id")] = { str: String ->
            str.replace(
                "%rumbling-reward-id:$id%",
                prop.currentReward?.id ?: ""
            )
        }
        binder.extraPlaceholders[Key.key("rumbling-reward-chance:$id")] = { str: String ->
            str.replace(
                "%rumbling-reward-chance:$id%",
                ((prop.currentReward?.chance ?: 0.0) * 100.0).decimals(2)
            )
        }

        binder.extraPlaceholders[Key.key("rumbling-reward-rarity-name:$id")] = { str: String ->
            str.replace(
                "%rumbling-reward-rarity-name:$id%",
                prop.currentReward?.rarity?.displayName ?: ""
            )
        }
        binder.extraPlaceholders[Key.key("rumbling-reward-rarity-id:$id")] = { str: String ->
            str.replace(
                "%rumbling-reward-rarity-id:$id%",
                prop.currentReward?.rarity?.rarityId ?: ""
            )
        }

        binder.props[Key.key("rumbling-reward:$id")] = prop
        prop.tick()
    }
}