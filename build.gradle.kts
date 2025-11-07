plugins {
    kotlin("jvm") version "2.2.21"
    id("com.gradleup.shadow") version "9.2.2"
    id("co.uzzu.dotenv.gradle") version "2.0.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    java

}

val projectVersion = "3.2.3"
group = "gg.aquatic.aquaticcrates"
version = projectVersion

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "xyz.jpenilla.run-paper")
    apply(plugin = "java")

    version = projectVersion

    repositories {
        maven {
            name = "undefined-repo"
            url = uri("https://repo.undefinedcreations.com/releases")
        }
        maven {
            name = "undefined-snapshots"
            url = uri("https://repo.undefinedcreations.com/snapshots")
        }
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        mavenCentral()
        mavenLocal()
        maven(url = "https://mvn.lumine.io/repository/maven-public/")

        maven("https://jitpack.io")
        maven {
            url = uri("https://repo.nekroplex.com/releases")
        }
        maven {
            url = uri("https://repo.codemc.io/repository/maven-releases/")
        }
        maven {
            url = uri("https://repo.codemc.io/repository/maven-snapshots/")
        }
        maven {
            url = uri("https://repo.hibiscusmc.com/releases")
        }
        maven("https://repo.auxilor.io/repository/maven-public/")
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
        compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.2-beta-r3-b")
        compileOnly("gg.aquatic.comet:Comet-API:1.13.0")
        compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.8")
        compileOnly("gg.aquatic.waves:Waves:1.3.28:publish")
        compileOnly("io.github.toxicity188:bettermodel:1.11.3")
        compileOnly("com.hibiscusmc:HMCCosmetics:2.8.1-1ef7f475")
        compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
        compileOnly("com.undefined:stellar-kotlin:1.1.1:paper")
        compileOnly("com.undefined:stellar:1.1.1:paper")
        compileOnly("com.willfp:EcoItems:5.66.0")
        compileOnly("com.willfp:eco:6.77.1")
        //implementation("net.kyori:adventure-api:4.17.0")
    }

    kotlin {
        jvmToolchain(21)
    }

    tasks {
        runServer {
            minecraftVersion("1.21.4")
        }
    }
}