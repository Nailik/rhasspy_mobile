plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared-viewmodel"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared-logic"))
                implementation(project(":shared-resources"))
                implementation(Touchlab.kermit)
                implementation(Jetbrains.Kotlinx.coroutines)
                implementation(Jetbrains.Kotlinx.dateTime)
                implementation(Koin.core)
                implementation(Icerock.Mvvm.core)
                implementation(Ktor2.Client.core)
                implementation(Icerock.Resources)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.multidex)
            }
        }
        val androidTest by getting
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
    namespace = "org.rhasspy.mobile.viewmodel"
    compileSdk = 33
    defaultConfig {
        minSdk = 23
    }
}