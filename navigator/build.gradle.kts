plugins {
    id(libs.plugins.convention.library.get().pluginId)
    id(libs.plugins.convention.compose.library.get().pluginId)
    alias(libs.plugins.ksp)
}

dependencies {
    //navigation
    api(projects.navigatorAndroid)
    ksp(projects.navigatorCodegen)
}
