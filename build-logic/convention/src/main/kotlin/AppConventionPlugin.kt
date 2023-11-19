import com.android.build.api.dsl.ApplicationExtension
import dev.funkymuse.fosho.commonVersioning
import dev.funkymuse.fosho.configureAppPluginPackageAndNameSpace
import dev.funkymuse.fosho.configureBuildFeatures
import dev.funkymuse.fosho.configureJavaCompatibilityCompileOptions
import dev.funkymuse.fosho.configureKotlinOptions
import dev.funkymuse.fosho.getPluginId
import dev.funkymuse.fosho.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(versionCatalog.getPluginId("android"))
                apply(versionCatalog.getPluginId("kotlinAndroid"))
                apply(versionCatalog.getPluginId("ksp"))
                apply(versionCatalog.getPluginId("convention-hilt"))
                apply(versionCatalog.getPluginId("convention-compose-app"))
            }
            configureKotlinOptions()
            extensions.configure<ApplicationExtension>{
                commonVersioning(this)
                configureAppPluginPackageAndNameSpace(this)
                configureBuildFeatures()
                configureJavaCompatibilityCompileOptions(this)
            }
        }
    }
}