
import dev.funkymuse.fosho.getBundle
import dev.funkymuse.fosho.getPluginId
import dev.funkymuse.fosho.implementation
import dev.funkymuse.fosho.ksp
import dev.funkymuse.fosho.kspPluginId
import dev.funkymuse.fosho.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = versionCatalog
            pluginManager.apply(libs.getPluginId("hilt"))
            pluginManager.apply(versionCatalog.kspPluginId)
            dependencies {
                add(implementation, libs.getBundle("hilt"))
                add(ksp, libs.getBundle("hilt-ksp"))
            }
        }
    }
}