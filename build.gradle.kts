plugins {
    kotlin("jvm") version "2.1.10"
    id("com.gradleup.shadow") version "9.0.0-beta11"
    id("co.uzzu.dotenv.gradle") version "2.0.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    java

}

val projectVersion = "3.1.8-Beta"
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
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
        compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.2-beta-r3-b")
        compileOnly("gg.aquatic.comet:Comet-API:1.13.0")
        compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.8")
        compileOnly("gg.aquatic.waves:Waves:1.3.10:publish")
        compileOnly("io.github.toxicity188:BetterModel:1.10.1")
        compileOnly("com.hibiscusmc:HMCCosmetics:2.8.1-1ef7f475")
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