package gg.aquatic.aquaticcrates.plugin.animation.pouch

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import gg.aquatic.aquaticseries.lib.audience.GlobalAudience
import gg.aquatic.aquaticseries.lib.util.executeActions
import org.bukkit.Location
import org.bukkit.entity.Player

class InstantPouchAnimationImpl(
    override val player: Player,
    override val animationManager: PouchAnimationManager,
    override val baseLocation: Location,
    override val rewards: MutableList<RolledReward>,
) : PouchAnimation() {
    override var state: State = State.PRE_OPEN
        private set


    override val props: MutableMap<String, AnimationProp> = hashMapOf()

    override val audience: AquaticAudience = GlobalAudience()

    /*
    val randomReward = animationManager.pouch.getRandomRewards(player,1).values.first().first
    val randomAmount = randomReward.amountRanges.randomItem()?.randomNum ?: 1

     */

    override fun tick() {
        finalize()
        /*
        when (state) {
            State.PRE_OPEN -> {
                tickPreOpen()
                if (tick >= animationManager.preAnimationDelay) {
                    updateState(State.OPENING)
                    tick()
                    return
                }
            }

            State.OPENING -> {
                tickOpening()
                if (tick >= animationManager.animationLength) {
                    updateState(State.POST_OPEN)
                    tick()
                    return
                }
            }

            State.POST_OPEN -> {
                tickPostOpen()
                if (tick >= animationManager.postAnimationDelay) {
                    finalize()
                    return
                }
            }

            else -> {
                return
            }
        }

        tick++
         */
    }

    private fun updateState(state: State) {
        this.state = state
        tick = 0
    }

    private fun finalize() {
        updateState(State.FINISHED)
        executeActions(animationManager.animationSettings.finalAnimationTasks)
        animationManager.playingAnimations.remove(player.uniqueId)
        for (reward in rewards) {
            reward.give(player)
        }
        //.give(player, randomAmount)
    }

    override fun executeActions(actions: List<ConfiguredAction<Animation>>) {
        actions.executeActions(this) { _, str ->
            var finalString = str.replace("%player%", player.name)
            for ((i, reward) in rewards.withIndex()) {
                finalString = finalString.replace("%random-amount:$i",reward.randomAmount.toString())
            }
            finalString
        }
    }
}