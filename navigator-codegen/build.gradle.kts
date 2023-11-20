plugins {
    id(libs.plugins.convention.kotlin.library.get().pluginId)
}

dependencies {
    implementation(libs.ksp.symbol.processing.api)
    implementation(libs.squareup.kotlinpoet)
    testImplementation(libs.bundles.processor.unit.tests)
}