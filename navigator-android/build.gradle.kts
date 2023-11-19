plugins {
    id(libs.plugins.convention.library.get().pluginId)
    id(libs.plugins.convention.compose.library.get().pluginId)
}

dependencies {
    ///nav
    api(libs.androidx.navigation.compose)
    api(projects.navigatorCodegen)
    api(libs.accompanist.navigationMaterial)
}
