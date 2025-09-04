import org.ajoberstar.grgit.Grgit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("java")
    id("org.ajoberstar.grgit") version "5.0.0-rc.3"
}

val grgit: Grgit = extensions.getByType<Grgit>()

val rawBranch: String = grgit.branch.current().name
val branch = rawBranch.replace(Regex("[^A-Za-z0-9._-]"), "_")

val commit: String = grgit.head().abbreviatedId

val timestamp: String by extra {
    LocalDateTime
        .now()
        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd_HH.mm.ss"))
}

group = "au.lupine"
version = "$branch-$commit-$timestamp"

subprojects {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "java")

    tasks.jar {
        archiveFileName.set("earthy-${project.name}-${project.version}.jar")

        from("${rootDir}/LICENSE.txt") {
            into("")
        }
    }

    base {
        archivesName.set("${rootProject.name}-${project.name}-${project.version}")
    }

    repositories {
        mavenCentral()
    }
}

tasks.jar {
    enabled = false
}
