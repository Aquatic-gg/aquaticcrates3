package gg.aquatic.aquaticseries.interactable

import gg.aquatic.aquaticseries.interactable.AbstractSpawnedInteractable

class SpawnedRegistry {

    val parents = HashMap<String, AbstractSpawnedInteractable>()
    val children = HashMap<String,String>()

}