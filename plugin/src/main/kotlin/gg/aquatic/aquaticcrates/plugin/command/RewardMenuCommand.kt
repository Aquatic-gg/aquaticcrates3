package gg.aquatic.aquaticcrates.plugin.command

import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.reward.menu.RewardsMenu
import gg.aquatic.waves.command.ICommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object RewardMenuCommand : ICommand {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (sender !is Player) return
        val menu = RewardsMenu((CratesPlugin.INSTANCE as CratesPlugin).rewardsMenuSettings, sender)
        menu.open()
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return emptyList()
    }
}