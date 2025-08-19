import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
}

group = "gg.aquatic.aquaticcrates.api"

repositories {
    mavenCentral()
}


val maven_username = if (env.isPresent("MAVEN_USERNAME")) env.fetch("MAVEN_USERNAME") else ""
val maven_password = if (env.isPresent("MAVEN_PASSWORD")) env.fetch("MAVEN_PASSWORD") else ""

publishing {
    repositories {
        maven {
            name = "aquaticRepository"
            url = uri("https://repo.nekroplex.com/releases")

            credentials {
                username = maven_username
                password = maven_password
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "gg.aquatic.aquaticcrates"
            artifactId = "AquaticCrates-API"
            version = "${project.version}"
            from(components["java"])
            /*
            artifact(tasks["shadowJarPublish"]) {
                classifier = "publish"
            }
            artifact(tasks["shadowJarPlugin"]) {
                classifier = "plugin"
            }
             */
        }
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xnon-local-break-continue"))
}