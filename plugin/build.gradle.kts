group = "gg.aquatic.aquaticcrates.plugin"

dependencies {
    implementation(project(":api"))
    //implementation("gg.aquatic.aquaticseries","aquatic-lib","1.0")
}

tasks {

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
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

    dependencies {
        include(project(":api"))
    }

    // Relocate packages
    relocate("kotlinx.coroutines", "gg.aquatic.waves.shadow.kotlinx.coroutines")
}