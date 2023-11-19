import com.android.build.gradle.LibraryExtension
import dev.funkymuse.fosho.addLibrariesConfig
import dev.funkymuse.fosho.commonVersioning
import dev.funkymuse.fosho.configureBuildFeatures
import dev.funkymuse.fosho.configureJavaCompatibilityCompileOptions
import dev.funkymuse.fosho.configureKotlinOptions
import dev.funkymuse.fosho.configureLibraryAndTestNameSpace
import dev.funkymuse.fosho.getPluginId
import dev.funkymuse.fosho.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class LibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(versionCatalog.getPluginId("library"))
                apply(versionCatalog.getPluginId("kotlinAndroid"))
            }
            configureKotlinOptions()
            configureLibraryAndTestNameSpace()
            extensions.configure<LibraryExtension> {
                commonVersioning(this)
                configureBuildFeatures()
                addLibrariesConfig()
                configureJavaCompatibilityCompileOptions(this)
            }
        }
    }
}