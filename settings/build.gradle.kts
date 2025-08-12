@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

plugins {
    id("base.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":data"))
            implementation(project(":platformspecific"))
            implementation(libs.kotlin.stdlib.common)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kermit)
            implementation(libs.koin.core)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.multiplatform.settings.serialization)
            implementation(libs.okio)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.rhasspy.mobile.settings"
}