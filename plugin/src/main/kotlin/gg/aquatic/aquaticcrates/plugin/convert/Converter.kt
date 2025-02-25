package gg.aquatic.aquaticcrates.plugin.convert

import gg.aquatic.waves.util.Config

interface Converter {

    fun convert(config: Config)
}

val CONVERTERS = hashMapOf<String, Converter>(
    "ac2" to AC2Converter
)