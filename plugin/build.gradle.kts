group = "gg.aquatic.aquaticcrates.plugin"

repositories {
    maven("https://repo.nexomc.com/releases")
    maven("https://repo.momirealms.net/releases/")
}

dependencies {
    implementation(project(":api"))
    //implementation("gg.aquatic.aquaticseries","aquatic-lib","1.0")
    compileOnly("com.nexomc:nexo:1.8.0") //Nexo 1.X -> 1.X.0

    compileOnly("net.momirealms:craft-engine-core:0.0.61")
    compileOnly("net.momirealms:craft-engine-bukkit:0.0.61")
}

sourceSets {
    main {
        kotlin {
            srcDir("src/main/kotlin")
        }
        java {
            srcDir("src/main/java")
        }
    }
}

version = parent!!.version

tasks {

    withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }



    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("paper-plugin.yml") {
            expand(getProperties())
            expand(mutableMapOf("version" to parent!!.version))
        }
    }

    val sourcesJar by registering(Jar::class) {
        ->
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    // Or specifically for sourcesJar if it's defined separately
    named<Jar>("sourcesJar") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    artifacts {
        archives(sourcesJar)
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("AquaticCrates-${project.version}.jar")
    archiveClassifier.set("all")

    exclude("kotlin/**")
    exclude("org/intellij/**")
    exclude("org/jetbrains/**")

    relocate("kotlinx", "gg.aquatic.waves.libs.kotlinx")
    relocate("org.jetbrains.kotlin", "gg.aquatic.waves.libs.kotlin")
    relocate("kotlin", "gg.aquatic.waves.libs.kotlin")
    relocate("org.bstats", "gg.aquatic.waves.shadow.bstats")
    //relocate("com.undefined", "gg.aquatic.waves.shadow.undefined")

    relocate("com.zaxxer.hikari", "gg.aquatic.waves.libs.hikari")
}