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
    id("base.android.app")
    alias(libs.plugins.google)
    alias(libs.plugins.firebase)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
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
    compileSdk = 36

    defaultConfig {
        applicationId = "org.rhasspy.mobile.android"
        targetSdk = 36
        minSdk = 23
        versionCode = Version.CODE
        versionName = Version.toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"
        testInstrumentationRunnerArguments["disableAnalytics"] = "false"
    }

    androidResources {
        localeFilters += listOf("en", "de", "it")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
            //enableUnitTestCoverage = true
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

    splits {
        abi {
            // Detect app bundle and conditionally disable split abis
            // This is needed due to a "Sequence contains more than one matching element" error
            // present since AGP 8.9.0, for more info see:
            // https://issuetracker.google.com/issues/402800800

            // AppBundle tasks usually contain "bundle" in their name
            //noinspection WrongGradleMethod
            val isBuildingBundle = gradle.startParameter.taskNames.any { it.lowercase().contains("bundle") }

            // Disable split abis when building appBundle
            isEnable = !isBuildingBundle
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = false
        }
    }

    packaging {
        //else netty finds multiple INDEX.LIST files
        resources.excludes.add("META-INF/LICENSE*.md")
        resources.excludes.add("META-INF/versions/9/previous-compilation-data.bin")
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.pickFirsts.add("META-INF/io.netty.versions.properties")
        resources.pickFirsts.add("META-INF/.*")
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
    coreLibraryDesugaring(libs.desugar)

    implementation(project(":app"))
    implementation(project(":viewmodel"))
    implementation(project(":logic"))
    implementation(project(":resources"))
    implementation(project(":platformspecific"))
    implementation(project(":data"))
    implementation(project(":ui"))
    implementation(project(":settings"))
    implementation(project(":widget"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.window)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kermit)
    implementation(libs.koin.core)
    implementation(libs.okio)
    implementation(libs.multiplatform.settings.no.arg)
    implementation(libs.moko.mvvm.core)

    androidTestUtil(libs.androidx.test.orchestrator)

    androidTestImplementation(libs.androidx.activity.compose)
    androidTestImplementation(libs.androidx.test.uiautomator)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.kotlin.test.junit)
    androidTestImplementation(libs.compose.test.junit4)
    androidTestImplementation(libs.adevinta.barista)
    androidTestImplementation(libs.hamcrest)
    androidTestImplementation(libs.compose.ui)
    androidTestImplementation(libs.compose.material3)
    androidTestImplementation(libs.kotlinx.collections.immutable)
    androidTestImplementation(libs.moko.mvvm.core)
    androidTestImplementation(libs.kermit)

    debugImplementation(libs.androidx.tracing.ktx)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}