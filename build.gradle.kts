plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
}

version = project.property("mod_version")!!
group = project.property("maven_group")!!

base {
    archivesName = project.property("archives_base_name")!!.toString()
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    exclusiveContent {
        forRepository {
            maven("\"https://api.modrinth.com/maven\"")
        }
        filter {
            includeGroup ("maven.modrinth")
        }
    }
    maven("https://maven.wispforest.io/releases")
    maven("https://maven.su5ed.dev/releases")
    maven("https://maven.fabricmc.net")
    maven("https://maven.shedaniel.me/")
    exclusiveContent {
        forRepository {
            maven("https://jitpack.io")
        }
        filter {
            includeGroupAndSubgroups ("com.github.kdl-org")
        }
    }
    mavenLocal()
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft ("com.mojang:minecraft:${project.property("minecraft_version")!!}")
    implementation ("net.fabricmc:fabric-loader:${project.property("loader_version")!!}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    implementation ("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")!!}")

    implementation("io.wispforest:accessories-fabric:1.4.4-beta+26.2")
    implementation("io.wispforest:accessories-common:1.4.4-beta+26.2")
    implementation("io.wispforest:owo-lib:0.13.0+26.1")
    annotationProcessor("io.wispforest:owo-lib:0.13.0+26.1")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to project.version))
    }
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

loom {
    runs {

       named("datagen") {
           name = "Data Generation"
           vmArg("-Dfabric-api.datagen")
           vmArg("-Dfabric-api.datagen.output-dir=${file("src/main/generated")}")

           runDir("build/datagen")
       }

       named("client")  {
           name = "Client"
           vmArg("-Dfabric.development=false")

           runDir("run")
       }
    }

    accessWidenerPath = file("src/main/resources/playercollars.accesswidener")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    // withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}"}
    }
    exclude("org/jlortiz/playercollars/datagen/**")
    exclude("assets/playercollars/lang/en_us.existing.json")
}