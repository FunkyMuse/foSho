plugins {
    id(libs.plugins.convention.app.get().pluginId)
}

android {
    buildFeatures.apply {
        buildConfig = true
    }

    buildTypes {
        debug {
            isCrunchPngs = false
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

ksp {
    arg("foSho.injectViewModelArguments", "true")
}

dependencies {

    //navigation
    implementation(projects.navigatorAndroid)
    ksp(projects.navigatorCodegen)

    //core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appCompat)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.androidx.splashscreen)

    //Compose
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    debugImplementation(libs.bundles.compose.preview)

    //lifecycle
    implementation(libs.bundles.lifecycle)
}