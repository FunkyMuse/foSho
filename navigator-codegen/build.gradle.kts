plugins {
    id(libs.plugins.convention.kotlin.library.get().pluginId)
    alias(libs.plugins.jetbrainsCompose)
}

dependencies {
    implementation(libs.ksp.symbol.processing.api)
    implementation(libs.squareup.kotlinpoet)
    implementation(libs.compose.runtime)
}