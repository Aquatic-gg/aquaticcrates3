package gg.aquatic.aquaticcrates.plugin.animation.action.showcase

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.plugin.animation.prop.showcase.RewardShowcaseAnimationProp
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.Action
import net.kyori.adventure.key.Key

@RegisterAction("update-reward-showcase")
class UpdateRewardShowcaseAction : Action<CrateAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("reward-id", "example", true),
    )

    override fun execute(
        binder: CrateAnimation,
        args: ObjectArguments,
        textUpdater: (CrateAnimation, String) -> String
    ) {
        binder as CrateAnimation

        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val rewardId = args.string("reward-id") { textUpdater(binder, it) } ?: return
        val prop = binder.props[Key.key("reward-showcase:$id")] as? RewardShowcaseAnimationProp ?: return

        val crate = binder.animationManager.crate as OpenableCrate
        val reward = crate.rewardManager.rewards[rewardId] ?: return
        val showcase = reward.showcase ?: crate.defaultRewardShowcase ?: return


        prop.showcaseHandle?.showcase?.despawnActions?.executeActions(binder) { p, str ->
            textUpdater(
                p, prop.showcaseHandle?.reward?.updatePlaceholders(str)
                    ?: str
            )
        }
        prop.update(reward, showcase)
        showcase.spawnActions.executeActions(binder) { p, str -> textUpdater(p, reward.updatePlaceholders(str)) }
    }
}