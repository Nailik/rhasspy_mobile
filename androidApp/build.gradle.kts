import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

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
        versionCode = Version.code
        versionName = Version.toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
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
        //else netty finds multiple INDEX.LIST files
        resources.pickFirsts.add("META-INF/INDEX.LIST")
        resources.pickFirsts.add("META-INF/io.netty.versions.properties")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
        testOptions.animationsDisabled = true
    }

    lint {
        //used to trust self signed certificates eventually
        disable.add("TrustAllX509TrustManager")
    }

    applicationVariants.all {
        this.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                output.outputFileName = output.outputFileName
                    .replace("androidApp", "rhasspy_mobile_V_$Version")
                    .replace("-release-unsigned", "")
                    .replace("-debug-unsigned", "")
            }
    }

}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}


tasks.withType<Test> {
    testLogging {
        events(STARTED, PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR)
        exceptionFormat = FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
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
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)
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
    implementation(AndroidX.Lifecycle.common)
    implementation(AndroidX.Lifecycle.common)
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

    implementation(Touchlab.kermit)
    implementation(AndroidX.lifecycle.process)
    implementation(Devsrsouza.fontAwesome)
    implementation(Mikepenz.aboutLibrariesCore)

    androidTestImplementation(Kotlin.test)
    androidTestImplementation(Kotlin.Test.junit)
    androidTestImplementation(AndroidX.Test.Espresso.core)
    androidTestImplementation(AndroidX.Compose.Ui.testJunit4)
    debugImplementation(AndroidX.Compose.Ui.testManifest)
}