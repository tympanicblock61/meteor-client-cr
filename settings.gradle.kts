pluginManagement {
    repositories {
        maven {
            name = "JitPack"
            url = uri("https://jitpack.io")
        }
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

buildscript {
    repositories {
        maven {
            name = "JitPack"
            url = uri("https://jitpack.io")
        }
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.github.johnrengelman:shadow:8.1.1")
        classpath("com.github.PuzzleLoader:jigsaw:${providers.gradleProperty("jigsaw_gradle_version").get()}")
    }
}


include("launch")
