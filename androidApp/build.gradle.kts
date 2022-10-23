@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT
import org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("C:\\SynologyDrive\\Dokumente\\Dokumente\\AppKeystores\\rhasspymobile.jks")
            keyAlias = "rhasspymobile"
            storePassword = "cK2JcFWUye4f%n$!GY9p"
            keyPassword = "cK2JcFWUye4f%n$!GY9p"
        }
    }
    compileSdk = 33

    defaultConfig {
        applicationId = "org.rhasspy.mobile.android"
        minSdk = 23
        targetSdk = 33
        versionCode = Version.code
        versionName = Version.toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments.put("clearPackageData", "true")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    lint {
        //used to trust self signed certificates eventually
        disable.add("TrustAllX509TrustManager")
    }
    namespace = "org.rhasspy.mobile.android"

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
    implementation("androidx.test.uiautomator:uiautomator:2.2.0")
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)
    implementation(project(":MultiPlatformLibrary"))

    implementation(KotlinX.Coroutines.core)
    implementation(KotlinX.Coroutines.android)

    implementation(Google.accompanist.systemUiController)

    implementation(AndroidX.Activity.compose)
    implementation(AndroidX.Core.splashscreen)
    implementation(AndroidX.ConstraintLayout.compose)
    implementation(AndroidX.Navigation.compose)

    implementation(AndroidX.Compose.material3)
    implementation(AndroidX.Compose.material.icons.extended)
    implementation(AndroidX.Compose.foundation)
    implementation(AndroidX.Compose.runtime.liveData)
    implementation(AndroidX.Lifecycle.viewModelCompose)
    implementation(AndroidX.Lifecycle.common)
    implementation(AndroidX.Compose.ui)
    implementation(AndroidX.Compose.ui.util)
    implementation(AndroidX.Compose.ui.tooling)

    implementation(AndroidX.multidex)
    implementation(AndroidX.window)

    implementation(Touchlab.kermit)
    implementation(Devsrsouza.fontAwesome)
    implementation(Mikepenz.aboutLibrariesCore)
    implementation(Icerock.Resources)
    implementation(Icerock.Mvvm.core)

    androidTestImplementation(project(":MultiPlatformLibrary"))
    androidTestImplementation(AndroidX.Test.runner)
    androidTestUtil(AndroidX.Test.orchestrator)
    androidTestImplementation(Kotlin.test)
    androidTestImplementation(Kotlin.Test.junit)
    androidTestImplementation(AndroidX.Test.coreKtx)
    androidTestImplementation(AndroidX.Compose.Ui.testJunit4)
    debugImplementation(AndroidX.Compose.Ui.testManifest)
}