subprojects {
    repositories {
        maven("https://maven.isxander.dev/releases")
        maven("https://jitpack.io")

        exclusiveContent {
            forRepository {
                maven("https://api.modrinth.com/maven")
            }
            filter {
                includeGroup("maven.modrinth")
            }
        }
    }

    val targetJava = 21
    java {
        val javaVersion = JavaVersion.toVersion(targetJava)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        if (JavaVersion.current() < javaVersion) {
            toolchain.languageVersion.set(JavaLanguageVersion.of(targetJava))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (targetJava >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJava)
        }
    }
}

tasks.jar {
    enabled = false
}
