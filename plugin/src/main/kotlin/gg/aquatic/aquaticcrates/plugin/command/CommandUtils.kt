package gg.aquatic.aquaticcrates.plugin.command

import com.undefined.stellar.AbstractStellarCommand
import com.undefined.stellar.argument.list.ListArgument
import com.undefined.stellar.kotlin.command
import com.undefined.stellar.kotlin.listArgument
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun registerCommands(builder: CommandBuilder.() -> Unit): CommandBuilder {
    return CommandBuilder().apply(builder)
}

class CommandBuilder internal constructor() {
    operator fun String.invoke(builder: AbstractStellarCommand<*>.() -> Unit) {
        command(this, builder = builder)
    }
}

fun AbstractStellarCommand<*>.allPlayersArgument(
    id: String,
    includeSelf: Boolean = false,
    block: ListArgument<Player?, String>.() -> Unit = {}
): ListArgument<Player?, String> {
    return this.listArgument(
        id,
        {
            Bukkit.getOnlinePlayers().mapNotNull {
                if (it == sender && !includeSelf) return@mapNotNull null
                it
            }
        },
        parse = { Bukkit.getPlayer(it) },
        converter = { it?.name },
        block = block
    )
}
