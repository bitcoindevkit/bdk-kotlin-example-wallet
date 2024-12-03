rootProject.name = "BDK Android Example Wallet"
include("app")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        // Snapshots repository
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

        // Local Maven (~/.m2/repository/)
        mavenLocal()
    }
}
