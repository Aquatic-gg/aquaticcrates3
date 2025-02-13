plugins {
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("co.uzzu.dotenv.gradle") version "2.0.0"
}

val projectVersion = "3.0.9-Beta"
group = "gg.aquatic.aquaticcrates"
version = projectVersion

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.github.johnrengelman.shadow")

    version = projectVersion

    repositories {
        mavenCentral()
        mavenLocal()
        maven(url = "https://mvn.lumine.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://jitpack.io")
        maven {
            url = uri("https://repo.nekroplex.com/releases")
        }
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
        compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.2-beta-r3-b")
        compileOnly("gg.aquatic.comet:Comet-API:1.4.0")
        compileOnly ("com.ticxo.modelengine:ModelEngine:R4.0.8")
        compileOnly("gg.aquatic.waves:Waves:1.1.34:publish")
        //implementation("net.kyori:adventure-api:4.17.0")
    }

    kotlin {
        jvmToolchain(17)
    }
}