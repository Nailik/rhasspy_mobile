@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("base.kmp.compose.library")
}

version = Version.toString()

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":ui"))
            implementation(project(":viewmodel"))
            implementation(project(":platformspecific"))
            implementation(libs.kermit)
            implementation(libs.koin.core)
            implementation(libs.moko.mvvm.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.multidex)
            implementation(libs.porcupine)
            implementation(libs.androidx.window)
            implementation(libs.androidx.appcompat)
            implementation(libs.compose.ui)
        }
    }
}

android {
    namespace = "org.rhasspy.mobile.overlay"
}