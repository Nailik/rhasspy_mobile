@file:Suppress("UNUSED_VARIABLE")

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
                implementation(project(":ui"))
                implementation(project(":viewmodel"))
                implementation(project(":platformspecific"))
                implementation(Touchlab.kermit)
                implementation(Koin.core)
                implementation(Icerock.Mvvm.core)
            }
        }
        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Kotlin.test)
                implementation(Koin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.multidex)
                implementation(Picovoice.porcupineAndroid)
                implementation(AndroidX.window)
                implementation(AndroidX.appCompat)
                implementation(AndroidX.Compose.ui)
            }
        }
        val androidUnitTest by getting {
            dependsOn(commonTest)
        }
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
    namespace = "org.rhasspy.mobile.overlay"
}