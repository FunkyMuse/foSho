import com.android.build.gradle.LibraryPlugin

plugins {
    id(libs.versions.gradlePlugins.maven.publish.get())
    alias(libs.plugins.android).apply(false)
    alias(libs.plugins.library).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.hilt).apply(false)
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.dokka)
}

subprojects {
    plugins.matching { anyPlugin -> supportedPlugins(anyPlugin) }.whenPluginAdded {
        apply(plugin = libs.versions.gradlePlugins.maven.publish.get())
        apply(plugin = libs.plugins.dokka.get().pluginId)

        plugins.withType<JavaLibraryPlugin> {
            publishing.publications {
                create<MavenPublication>("kotlin") {
                    groupId = libs.versions.app.version.groupId.get()
                    artifactId = this@subprojects.name
                    version = libs.versions.app.version.versionName.get()
                    afterEvaluate {
                        from(components["kotlin"])
                    }
                }
            }
        }
        plugins.withType<LibraryPlugin> {
            configure<com.android.build.gradle.BaseExtension> {
                configure<com.android.build.gradle.LibraryExtension> {

                    defaultConfig {
                        consumerProguardFiles("consumer-rules.pro")
                    }

                    buildTypes {
                        getByName("release") {
                            isMinifyEnabled = false
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                "proguard-rules.pro"
                            )
                        }
                    }
                    afterEvaluate {
                        publishing.publications {
                            create<MavenPublication>("release") {
                                groupId = libs.versions.app.version.groupId.get()
                                artifactId = this@subprojects.name
                                version = libs.versions.app.version.versionName.get()
                                afterEvaluate {
                                    from(components["release"])
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


fun supportedPlugins(anyPlugin: Plugin<*>?) =
    anyPlugin is LibraryPlugin || anyPlugin is JavaLibraryPlugin
