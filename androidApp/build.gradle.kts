plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "org.rhasspy.mobile.android"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "_"
    }
}

kotlin {
    sourceSets {

        all {
            //Warning: This class can only be used with the compiler argument '-opt-in=kotlin.RequiresOptIn'
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}

dependencies {
    implementation(project(":MultiPlatformLibrary"))

    implementation(Google.android.material)

    implementation(AndroidX.Activity.compose)
    implementation(AndroidX.ConstraintLayout.compose)
    implementation(AndroidX.Navigation.compose)

    implementation(AndroidX.Compose.material3)
    implementation(AndroidX.Compose.material.icons.extended)
    implementation(AndroidX.Compose.foundation)
    implementation(AndroidX.Compose.ui)
    implementation(AndroidX.Compose.ui.tooling)
    implementation(AndroidX.Compose.ui.toolingPreview)

    implementation(Icerock.Mvvm.core)
    implementation(Icerock.Mvvm.state)
    implementation(Icerock.Mvvm.livedata)
    implementation(Icerock.Mvvm.livedataResources)

}