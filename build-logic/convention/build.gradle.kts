plugins {
    `kotlin-dsl`
}

group = libs.versions.app.version.appId

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.app.build.kotlinJVMTarget.get()))
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidConventionComposeLibrary") {
            id = "convention.android.compose.library"
            implementationClass = "LibraryComposeConventionPlugin"
        }
        register("androidConventionComposeApp") {
            id = "convention.android.compose.app"
            implementationClass = "AppComposeConventionPlugin"
        }
        register("appConventionPlugin") {
            id = "convention.android.app"
            implementationClass = "AppConventionPlugin"
        }
        register("libraryConventionPlugin") {
            id = "convention.android.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("kotlinConventionLibraryPlugin") {
            id = "convention.android.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("hiltConventionPlugin") {
            id = "convention.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }
    }
}
