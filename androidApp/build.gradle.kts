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
        minSdk = 23
        targetSdk = 36
        versionCode = Version.code
        versionName = Version.toString()
        resourceConfigurations += setOf("en", "de", "it")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"
        testInstrumentationRunnerArguments["disableAnalytics"] = "false"
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-P=plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_metrics",
            "-P=plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_metrics"
        )
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
    debugImplementation(libs.compose.ui.test.manifest)
}