import dev.funkymuse.fosho.configureJava
import dev.funkymuse.fosho.getPluginId
import dev.funkymuse.fosho.getVersion
import dev.funkymuse.fosho.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion

class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(versionCatalog.getPluginId("java-library"))
                apply(versionCatalog.getPluginId("kotlinJvm"))
            }
            configureJava {
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(versionCatalog.getVersion("app-build-kotlinJVMTarget")))
                }
            }
        }
    }
}