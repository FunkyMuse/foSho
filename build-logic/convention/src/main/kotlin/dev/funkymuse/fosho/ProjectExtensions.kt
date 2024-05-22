package dev.funkymuse.fosho

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.utils.named

fun CommonExtension<*, *, *, *, *, *>.configureBuildFeatures() {
    buildFeatures.apply {
        resValues = false
        shaders = false
    }
}

fun LibraryExtension.addLibrariesConfig() {
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
}


fun Project.configureKotlinOptions() {
    tasks
        .withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
            compilerOptions {
                freeCompilerArgs.addAll(
                    listOf(
                        "-opt-in=kotlin.RequiresOptIn",
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=kotlinx.coroutines.FlowPreview",
                        "-opt-in=androidx.compose.ui.text.ExperimentalTextApi",
                        "-opt-in=com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi",
                        "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                        "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                        "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
                        "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                        "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
                        "-opt-in=com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi",
                        "-Xcontext-receivers"
                    )
                )
            }
        }
}

fun Project.configureJavaCompatibilityCompileOptions(commonExtensions: CommonExtension<*, *, *, *, *, *>) {
    commonExtensions.compileOptions {
        val currentJavaVersionFromLibs = JavaVersion.valueOf(
            versionCatalog.findVersion("app-build-javaVersion").get().toString()
        )
        sourceCompatibility = currentJavaVersionFromLibs
        targetCompatibility = currentJavaVersionFromLibs
    }
}

fun Project.configureAppPluginPackageAndNameSpace(
    commonExtensions: ApplicationExtension
) {
    commonExtensions.apply {
        namespace = packageName
        defaultConfig {
            applicationId = namespace
        }
    }
}

fun Project.configureLibraryAndTestNameSpace() {
    configure<BaseExtension> {
        namespace = versionCatalog.getVersion("app-version-groupId")
            .plus(path.replace(":", ".").replace("-", "."))
    }
}

val Project.packageName get() = versionCatalog.getVersion("app-version-appId")

fun Project.commonVersioning(libraryExtension: LibraryExtension) {
    libraryExtension.apply {
        setCompileSdkVersion(getCompileSDKVersion())
        defaultConfig {
            minSdk = getMinSDKVersion()
            testInstrumentationRunner = getTestRunner()
        }
    }
}

fun Project.commonVersioning(libraryExtension: TestExtension) {
    libraryExtension.apply {
        setCompileSdkVersion(getCompileSDKVersion())
        defaultConfig {
            minSdk = getMinSDKVersion()
            testInstrumentationRunner = getTestRunner()
        }
    }
}

fun Project.commonVersioning(libraryExtension: ApplicationExtension) {
    libraryExtension.apply {
        compileSdk = getCompileSDKVersion()
        defaultConfig {
            minSdk = getMinSDKVersion()
            targetSdk = getMinSDKVersion()
            testInstrumentationRunner = getTestRunner()
            versionName = versionCatalog.getVersion("app-version-versionName")
            versionCode = versionCatalog.getVersion("app-version-versionCode").toInt()
        }
    }
}

fun Project.getTestRunner() = versionCatalog.getVersion("app-build-testRunner")

fun Project.getMinSDKVersion() = versionCatalog.getVersion("app-build-minimumSDK").toInt()
fun Project.getCompileSDKVersion() =
    versionCatalog.getVersion("app-build-compileSDKVersion").toInt()

