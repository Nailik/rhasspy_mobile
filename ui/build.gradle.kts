@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("base-gradle")
}

version = Version.toString()

kotlin {

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ui"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":viewmodel"))
                implementation(project(":data"))
                implementation(project(":resources"))
                implementation(project(":settings"))
                implementation(Icerock.Resources.resourcesCompose)
                implementation(Jetbrains.Compose.ui)
                implementation(Jetbrains.Compose.foundation)
                implementation(Jetbrains.Compose.material)
                implementation(Jetbrains.Compose.material3)
                implementation(Jetbrains.Compose.runtime)
                implementation(Jetbrains.Compose.materialIconsExtended)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.Compose.ui)
                implementation(Google.accompanist.systemUiController)
                implementation(AndroidX.core)
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

android {
    namespace = "org.rhasspy.mobile.ui"
    buildFeatures.compose = true
}