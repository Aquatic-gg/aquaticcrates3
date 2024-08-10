group = "gg.aquatic.aquaticcrates"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    dependencies {
        api(project(":plugin"))
        api(project(":api"))
    }
}

kotlin {
    jvmToolchain(21)
}