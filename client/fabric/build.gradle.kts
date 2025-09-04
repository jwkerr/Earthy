plugins {
    id("fabric-loom") version "1.11-SNAPSHOT"
}

val minecraft_version: String by project
val fabric_loader_version: String by project
val fabric_version: String by project
val modmenu_version: String by project
val yacl_fabric_version: String by project
val adventure_version: String by project
val xaeros_minimap_fabric_version: String by project

repositories {
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    include(implementation("com.github.jwkerr:EMCAPIClient:e36d646791")!!)

    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

    modImplementation("com.terraformersmc:modmenu:$modmenu_version")
    modImplementation("dev.isxander:yet-another-config-lib:$yacl_fabric_version")
    include(modImplementation("net.kyori:adventure-platform-fabric:$adventure_version")!!)
    modImplementation("maven.modrinth:xaeros-minimap:$xaeros_minimap_fabric_version")
}

tasks.processResources {
    inputs.property("version", version)
    inputs.property("minecraft_version", minecraft_version)
    inputs.property("loader_version", fabric_loader_version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to version,
                "minecraft_version" to minecraft_version,
                "loader_version" to fabric_loader_version
            )
        )
    }
}
