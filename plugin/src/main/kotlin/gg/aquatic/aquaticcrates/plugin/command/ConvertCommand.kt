package gg.aquatic.aquaticcrates.plugin.command

import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.convert.CONVERTERS
import gg.aquatic.waves.command.ICommand
import gg.aquatic.waves.util.Config
import gg.aquatic.waves.util.runAsync
import org.bukkit.command.CommandSender
import java.io.File

object ConvertCommand: ICommand {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission("aquaticcrates.admin")) return

        if (args.size < 3) {
            return sender.sendMessage("Usage: /convert <converter> <input file> <output file>")
        }

        val converter = CONVERTERS[args[1]] ?: return sender.sendMessage("Invalid converter: ${args[1]}")

        fun convert(file: File) {
            val time = System.currentTimeMillis()
            converter.convert(Config(file, CratesPlugin.INSTANCE).apply { load() })
            sender.sendMessage("Converted ${file.name} in ${System.currentTimeMillis() - time}ms")
        }

        val fileName = args[2]
        if (fileName == "*") {
            runAsync {
                CratesPlugin.INSTANCE.dataFolder.resolve("convert").listFiles()?.forEach {
                    convert(it)
                }
            }
            return
        }
        val inputFile = CratesPlugin.INSTANCE.dataFolder.resolve("convert").resolve(args[2])
        runAsync {
            convert(inputFile)
        }

    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> CONVERTERS.keys.toList()
            2 -> CratesPlugin.INSTANCE.dataFolder.resolve("convert").listFiles()?.map { it.name }?.toMutableList()?.apply {
                add("*")
            } ?: emptyList()
            else -> emptyList()
        }
    }
}