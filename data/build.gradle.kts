@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("base.kmp.library")
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":resources"))
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kermit)
            implementation(libs.okio)
            implementation(libs.aboutlibraries.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.rhasspy.mobile.data"
}