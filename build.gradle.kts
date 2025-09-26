plugins {
    id("java")
    id("jigsaw")
    id("com.github.johnrengelman.shadow")
//    id("fabric-loom") version "1.9-SNAPSHOT"
//    id("maven-publish")
//    id("com.gradleup.shadow") version "9.0.0-beta4"
}

base {
    archivesName = properties["archives_base_name"] as String

    val suffix = if (project.hasProperty("build_number")) {
         project.findProperty("build_number")
    } else {
        "local"
    }

    version = properties["cosmic_reach_version"] as String + "-" + suffix
}

repositories {
    maven {
        name = "meteor-maven"
        url = uri("https://maven.meteordev.org/releases")
    }
    maven {
        name = "meteor-maven-snapshots"
        url = uri("https://maven.meteordev.org/snapshots")
    }
    maven {
        name = "vram"
        url = uri("https://maven.vram.io//")
    }
    maven {
        name = "ViaVersion"
        url = uri("https://repo.viaversion.com")
    }
    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")
    }
    mavenCentral()

    exclusiveContent {
        forRepository {
            maven {
                name = "modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

val modInclude: Configuration by configurations.creating
val modCompileOnly: Configuration by configurations.creating
val library: Configuration by configurations.creating

configurations {
    // include mods
    implementation.configure {
        extendsFrom(modInclude)
    }
    include.configure {
        extendsFrom(modInclude)
    }

    // include libraries
    implementation.configure {
        extendsFrom(library)
    }
    shadow.configure {
        extendsFrom(library)
    }

    compileOnly.configure{
        extendsFrom(modCompileOnly)
    }
}

dependencies {
    cosmicReach("finalforeach:cosmicreach:${project.property("cosmic_reach_version")}")

    // Libraries
    library("meteordevelopment:orbit:${properties["orbit_version"] as String}")
    library("meteordevelopment:starscript:${properties["starscript_version"] as String}")
    library("meteordevelopment:discord-ipc:${properties["discordipc_version"] as String}")
    library("org.reflections:reflections:${properties["reflections_version"] as String}")
    library("io.netty:netty-handler-proxy:${properties["netty_version"] as String}") { isTransitive  = false }
    library("io.netty:netty-codec-socks:${properties["netty_version"] as String}") { isTransitive  = false }
    library("it.unimi.dsi:fastutil:${properties["fastutil_version"] as String}")
    library("commons-io:commons-io:${properties["appache_commons_io_version"] as String}")
    library("org.lwjgl:lwjgl-tinyfd:3.3.6")

    // Launch sub project
    shadow(project(":launch"))
}

loom {
    accessWidenerPath = file("src/main/resources/meteor-client.accesswidener")
}

tasks {
    processResources {
        val buildNumber = project.findProperty("build_number")?.toString() ?: ""
        val commit = project.findProperty("commit")?.toString() ?: ""

        val propertyMap = mapOf(
            "mod_version"           to project.version,
            "build_number"      to buildNumber,
            "commit"            to commit,
            "cosmic_reach_version" to project.property("cosmic_reach_version"),
            "puzzle_loader_version"    to project.property("puzzle_loader_version")
        )

        inputs.properties(propertyMap)
        filesMatching("fabric.mod.json") {
            expand(propertyMap)
        }
    }

    jar {
        val licenseSuffix = project.base.archivesName.get()
        from("LICENSE") {
            rename { "${it}_${licenseSuffix}" }
        }

        manifest {
            attributes["Main-Class"] = "meteordevelopment.meteorclient.Main"
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        if (System.getenv("CI")?.toBoolean() == true) {
            withSourcesJar()
            withJavadocJar()
        }
    }

    withType<JavaCompile> {
        options.release = 21
    }

    shadowJar {
        configurations = listOf(project.configurations.shadow.get())

        val licenseSuffix = project.base.archivesName.get()
        from("LICENSE") {
            rename { "${it}_${licenseSuffix}" }
        }

        dependencies {
            exclude {
                it.moduleGroup == "org.slf4j"
            }
        }
    }

    javadoc {
        with (options as StandardJavadocDocletOptions) {
            addStringOption("Xdoclint:none", "-quiet")
            addStringOption("encoding", "UTF-8")
            addStringOption("charSet", "UTF-8")
        }
    }

    build {
        if (System.getenv("CI")?.toBoolean() == true) {
            dependsOn("javadocJar")
        }
    }
}
