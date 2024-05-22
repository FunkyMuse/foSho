import com.android.build.gradle.LibraryExtension
import dev.funkymuse.fosho.configureAndroidCompose
import dev.funkymuse.fosho.getPluginId
import dev.funkymuse.fosho.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class LibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(versionCatalog.getPluginId("compose-compiler"))
            }
            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}