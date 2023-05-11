@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("base-gradle")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":data"))
                implementation(project(":platformspecific"))
                implementation(project(":resources"))
                implementation(project(":settings"))
                implementation(Jetbrains.Kotlinx.coroutines)
                implementation(Touchlab.kermit)
                implementation(Jetbrains.Kotlinx.serialization)
                implementation(Jetbrains.Kotlinx.immutable)
                implementation(Jetbrains.Kotlinx.dateTime)
                implementation(Icerock.Resources)
                implementation(Ktor2.Server.core)
                implementation(Ktor2.Server.cors)
                implementation(Ktor2.Server.cio)
                implementation(Ktor2.Server.dataConversion)
                implementation(Ktor2.Client.cio)
                implementation(Ktor2.Server.statusPages)
                implementation(Ktor2.Plugins.network)
                implementation(Ktor2.Server.core)
                implementation(Benasher.uuid)
                implementation(Square.okio)
                implementation(Koin.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Kotlin.test)
                implementation(Koin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.multidex)
                implementation(Picovoice.porcupineAndroid)
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
    namespace = "org.rhasspy.mobile.logic"
}
