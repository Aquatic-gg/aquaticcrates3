import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin.Companion.shadowJar

group = "gg.aquatic.aquaticcrates.plugin"

dependencies {
    implementation(project(":api"))
    //implementation("gg.aquatic.aquaticseries","aquatic-lib","1.0")
}

version = parent!!.version

tasks {

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

    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    artifacts {
        archives(sourcesJar)
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("AquaticCrates-${project.version}.jar")
    archiveClassifier.set("all")

    relocate("org.bstats", "gg.aquatic.aquaticcrates.shadow.bstats")
    exclude("kotlin/**")
    exclude("org/intellij/**")
    exclude("org/jetbrains/**")
    //relocate("kotlin", "gg.aquatic.waves.shadow.kotlin")
}