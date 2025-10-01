package gg.aquatic.aquaticcrates.plugin.command

import com.undefined.stellar.StellarCommand
import com.undefined.stellar.kotlin.*
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import org.bukkit.entity.Player

object BaseCommand : KotlinBaseStellarCommand(
    "aquaticcrates", "", listOf(
        "acrates",
        "crates",
        "crate"
    )
) {
    override fun setup(): StellarCommand = kotlinCommand {

        "key" {
            "give" {
                listArgument(
                    "crate",
                    { CrateHandler.crates.values },
                    parse = { CrateHandler.crates[it] },
                    { it?.identifier }) {
                    onlinePlayersArgument("player") {
                        asyncRunnable<Player> {
                            val crate: Crate by args
                            val player: Player by args
                            val amount = getOrNull<Int>("amount") ?: 1
                            val flags = getOrNull<String>("flags") ?: ""
                            giveKeys(player, crate, amount)
                            false
                        }
                        integerArgument("amount") {
                        }
                        intRangeArgument("amount") {
                        }
                        phraseArgument("flags") {
                            addWordSuggestions(0,"-v", "-s", "-off")
                            addWordSuggestions(1,"-v", "-s", "-off")
                            addWordSuggestions(2,"-v", "-s", "-off")
                        }
                    }
                }
            }
        }

        "crate" {

        }
    }


    private fun giveKeys(player: Player, crate: Crate, amount: Int) {

    }
}