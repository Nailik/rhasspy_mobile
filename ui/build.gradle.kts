@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("base-gradle")
}

version = Version.toString()

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":viewmodel"))
                implementation(project(":data"))
                implementation(project(":logic"))
                implementation(project(":resources"))
                implementation(project(":settings"))
                implementation(project(":platformspecific"))
                implementation(Icerock.Resources.resourcesCompose)
                implementation(Icerock.Mvvm.core)
                implementation(Koin.core)
                implementation(Touchlab.kermit)
                implementation(Jetbrains.Kotlinx.immutable)
                implementation(Jetbrains.Compose.ui)
                implementation(Jetbrains.Compose.foundation)
                implementation(Jetbrains.Compose.material)
                implementation(Jetbrains.Compose.material3)
                implementation(Jetbrains.Compose.runtime)
                implementation(Jetbrains.Compose.materialIconsExtended)
                implementation(Mikepenz.aboutLibrariesCore)
                implementation(Jetbrains.Kotlinx.dateTime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.Activity.compose)
                implementation(AndroidX.Compose.ui)
                implementation(AndroidX.Compose.Ui.toolingPreview)
                // Deprecated in favor of Activity.enableEdgeToEdge from androidx.activity 1.8+
                //FIXME: See the example PR in the migration guide here:
                // https://google.github.io/accompanist/systemuicontroller/
                implementation("com.google.accompanist:accompanist-systemuicontroller:_")
                implementation(AndroidX.core)
                implementation(AndroidX.multidex)
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }

}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi"
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
    kotlinOptions.freeCompilerArgs += "-P=plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_metrics"
    kotlinOptions.freeCompilerArgs += "-P=plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_metrics"
}

android {
    namespace = "org.rhasspy.mobile.ui"
    buildFeatures {
        compose = true
    }
}
