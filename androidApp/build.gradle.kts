@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT
import org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("base-gradle")
}

val signingProperties = Properties()
val singingFile = file("../signing.properties")
val signingEnabled = singingFile.exists()
if (signingEnabled) {
    signingProperties.load(singingFile.inputStream())
}

android {
    signingConfigs {
        if (signingEnabled) {
            create("release") {
                storeFile = file(signingProperties.getProperty("storeFile"))
                storePassword = signingProperties["storePassword"].toString()
                keyAlias = signingProperties["keyAlias"].toString()
                keyPassword = signingProperties["keyPassword"].toString()
            }
            getByName("debug") {
                storeFile = file(signingProperties.getProperty("storeFileDebug"))
                storePassword = signingProperties["storePasswordDebug"].toString()
                keyAlias = signingProperties["keyAliasDebug"].toString()
                keyPassword = signingProperties["keyPasswordDebug"].toString()
            }
        }
    }
    compileSdk = 34

    defaultConfig {
        applicationId = "org.rhasspy.mobile.android"
        minSdk = 23
        targetSdk = 34
        versionCode = Version.code
        versionName = Version.toString()
        resourceConfigurations += setOf("en", "de")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"
        testInstrumentationRunnerArguments["disableAnalytics"] = "false"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (signingEnabled) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            isDebuggable = true
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            isMinifyEnabled = false
            isShrinkResources = false
            if (signingEnabled) {
                signingConfig = signingConfigs.getByName("debug")
            }
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

    packaging {
        //else netty finds multiple INDEX.LIST files
        resources.pickFirsts.add("META-INF/INDEX.LIST")
        resources.pickFirsts.add("META-INF/io.netty.versions.properties")
        resources.pickFirsts.add("META-INF/.*")
        resources.excludes.add("META-INF/LICENSE*.md")
        resources.pickFirsts.add("BuildConfig.kt")
        resources.pickFirsts.add("BuildConfig.dex")
        jniLibs.keepDebugSymbols.add("**/libpv_porcupine.so")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    testOptions {
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
                    .replace("-debug", "")
            }
    }

}

configurations.all {
    resolutionStrategy {
        eachDependency {
            if ((requested.group == "org.jetbrains.kotlin") && (!requested.name.startsWith("kotlin-gradle"))) {
                useVersion("1.9.0-RC")
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi"
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
    kotlinOptions.freeCompilerArgs += "-P=plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_metrics"
    kotlinOptions.freeCompilerArgs += "-P=plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_metrics"
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

dependencies {
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)

    implementation(project(":app"))
    implementation(project(":viewmodel"))
    implementation(project(":logic"))
    implementation(project(":resources"))
    implementation(project(":platformspecific"))
    implementation(project(":data"))
    implementation(project(":ui"))
    implementation(project(":settings"))
    implementation(project(":widget"))

    implementation(AndroidX.appCompat)
    implementation(AndroidX.Core.splashscreen)
    implementation(AndroidX.Activity.compose)
    implementation(AndroidX.multidex)
    implementation(AndroidX.window)
    implementation(AndroidX.Core.ktx)
    implementation(Touchlab.kermit)
    implementation(Koin.core)
    implementation(Square.okio)
    implementation(Russhwolf.multiplatformSettingsNoArg)
    implementation(Icerock.Mvvm.core)
    implementation(Kotlin.test)
    implementation(Kotlin.Test.junit)

    implementation(Kotlin.test)
    implementation(AndroidX.Activity.compose)
    implementation(AndroidX.appCompat)

    androidTestUtil(AndroidX.Test.orchestrator)

    androidTestImplementation(AndroidX.Activity.compose)
    androidTestImplementation(Koin.test)
    androidTestImplementation(AndroidX.Test.uiAutomator)
    androidTestImplementation(AndroidX.Test.runner)
    androidTestImplementation(AndroidX.Test.rules)
    androidTestImplementation(Kotlin.test)
    androidTestImplementation(Kotlin.Test.junit)
    androidTestImplementation(AndroidX.Test.coreKtx)
    androidTestImplementation(Jetbrains.Compose.testJunit4)
    androidTestImplementation(Adevinta.barista)
    androidTestImplementation(Hamcrest.hamcrest)
    androidTestImplementation(Jetbrains.Compose.ui)
    androidTestImplementation(Jetbrains.Compose.material3)
    androidTestImplementation(Jetbrains.Kotlinx.immutable)
    androidTestImplementation(Icerock.Mvvm.core)
    androidTestImplementation(Touchlab.kermit)

    debugImplementation(AndroidX.tracing.ktx)
    debugImplementation(AndroidX.Compose.Ui.testManifest)
}