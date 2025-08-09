@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("base.kmp.compose.library")
}

version = Version.toString()

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":viewmodel"))
            implementation(project(":data"))
            implementation(project(":logic"))
            implementation(project(":resources"))
            implementation(project(":settings"))
            implementation(project(":platformspecific"))
            implementation(libs.moko.resources.compose)
            implementation(libs.moko.mvvm.core)
            implementation(libs.koin.core)
            implementation(libs.kermit)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.aboutlibraries.core)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.multidex)
        }
    }

}

android {
    namespace = "org.rhasspy.mobile.ui"
    buildFeatures {
        compose = true
    }
}