@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

plugins {
    id("base.kmp.compose.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":viewmodel"))
            implementation(project(":resources"))
            implementation(project(":platformspecific"))
            implementation(libs.koin.core)
            implementation(libs.moko.mvvm.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.glance.appwidget)
            implementation(libs.androidx.multidex)
        }
    }
}

android {
    namespace = "org.rhasspy.mobile.widget"

    buildFeatures.compose = true
}