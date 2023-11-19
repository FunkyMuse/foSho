pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "maven.publish" -> {
                    useModule("com.github.dcendents:android-maven-gradle-plugin:${requested.version}")
                }
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven {
            setUrl("https://androidx.dev/storage/compose-compiler/repository/")
        }
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "foSho"
include(":app")
include(":navigator-codegen")
include(":navigator-android")
