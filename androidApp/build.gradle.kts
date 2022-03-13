plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "org.rhasspy.mobile.android"
        minSdk = 23
        targetSdk = 32
        versionCode = 1
        versionName = "0.1"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "_"
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = false
        }
    }
    packagingOptions {
        resources.pickFirsts.add("META-INF/*")
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

    implementation(KotlinX.Coroutines.core)
    implementation(KotlinX.Coroutines.android)

    implementation(Google.android.material)
    implementation(Google.Accompanist.insets)
    implementation(Google.Accompanist.systemuicontroller)

    implementation(AndroidX.Activity.compose)
    implementation(AndroidX.Core.splashscreen)
    implementation(AndroidX.ConstraintLayout.compose)
    implementation(AndroidX.Navigation.compose)

    implementation(AndroidX.Compose.material3)
    implementation(AndroidX.Compose.material)
    implementation(AndroidX.Compose.material.icons.extended)
    implementation(AndroidX.Compose.foundation)
    implementation(AndroidX.Compose.runtime.liveData)
    implementation(AndroidX.Lifecycle.viewModelCompose)
    implementation(AndroidX.Compose.ui)
    implementation(AndroidX.Compose.ui.util)
    implementation(AndroidX.Compose.ui.tooling)
    implementation(AndroidX.Compose.ui.toolingPreview)

    implementation(AndroidX.multidex)
    implementation(AndroidX.window)

    implementation(Icerock.Mvvm.core)
    implementation(Icerock.Mvvm.state)
    implementation(Icerock.Mvvm.livedata)
    implementation(Icerock.Mvvm.livedataResources)

    implementation("co.touchlab:kermit:_")

}