@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("base-gradle")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":viewmodel"))
                implementation(project(":resources"))
                implementation(project(":platformspecific"))
                implementation(Koin.core)
                implementation(Icerock.Mvvm.core)
            }
        }
        val commonTest by getting {
            dependencies {}
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.glance.appWidget)
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

android {
    namespace = "org.rhasspy.mobile.widget"

    buildFeatures.compose = true
}