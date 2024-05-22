package dev.funkymuse.fosho

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import java.io.File

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    val libs = versionCatalog
    commonExtension.apply {
        buildFeatures.compose = true

        tasks
            .withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
                compilerOptions {
                    freeCompilerArgs.addAll(buildComposeMetricsParameters())
                }
            }

        dependencies {
            add(implementation, libs.getBundle("compose"))
            add(implementation, libs.getLibrary("kotlin-immutable-collections"))
            add(debugImplementation, libs.getBundle("compose-preview"))
        }
    }
}

val Project.composeMetricsDir get() = layout.buildDirectory.asFile.get().absolutePath + "/compose_metrics"
val Project.composeReportsDir get() = layout.buildDirectory.asFile.get().absolutePath + "/compose_reports"


private fun Project.buildComposeMetricsParameters(): List<String> {
    val metricParameters = mutableListOf<String>()
    val enableMetricsProvider = project.providers.gradleProperty("enableComposeCompilerMetrics")
    val enableMetrics = (enableMetricsProvider.orNull == "true")
    if (enableMetrics) {
        val metricsFolder = File(composeMetricsDir)
        metricParameters.add("-P")
        metricParameters.add(
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + metricsFolder.absolutePath
        )
    }

    val enableReportsProvider = project.providers.gradleProperty("enableComposeCompilerReports")
    val enableReports = (enableReportsProvider.orNull == "true")
    if (enableReports) {
        val reportsFolder = File(composeReportsDir)
        metricParameters.add("-P")
        metricParameters.add(
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + reportsFolder.absolutePath
        )
    }
    return metricParameters.toList()
}
