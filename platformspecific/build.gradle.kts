@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

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
                implementation(project(":resources"))
                implementation(Square.okio)
                implementation(Ktor2.Client.core)
                implementation(Koin.core)
                implementation(Icerock.Resources)
                implementation(Touchlab.kermit)
                implementation(Jetbrains.Kotlinx.serialization)
                implementation(Jetbrains.Kotlinx.immutable)
                implementation(Ktor2.Server.core)
                implementation(Ktor2.Server.cors)
                implementation(Ktor2.Server.cio)
                implementation(Ktor2.Server.dataConversion)
                implementation(Ktor2.Client.cio)
                implementation(Ktor2.Server.statusPages)
                implementation(Ktor2.Plugins.network)
                implementation(Ktor2.Server.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Square.Okio.jvm)
                implementation(Square.okio)
                implementation(AndroidX.appCompat)
                implementation(AndroidX.multidex)
                implementation(AndroidX.lifecycle.process)
                implementation(AndroidX.appCompat)
                implementation(Ktor2.Server.compression)
                implementation(Ktor2.Server.callLogging)
                implementation(Ktor2.Server.netty)
                implementation(Koin.android)
                implementation(Koin.androidCompat)
                implementation(Jetbrains.Kotlinx.dateTime)
                implementation(Picovoice.porcupineAndroid)
                implementation(files("libs/org.eclipse.paho.client.mqttv3-1.2.5.jar"))
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting {
            dependencies {
                implementation(Square.Okio.iosx64)
            }
        }
        val iosArm64Main by getting {
            dependencies {
                implementation(Square.Okio.iosarm64)
            }
        }
        val iosSimulatorArm64Main by getting {
            dependencies {
                implementation(Square.Okio.iossimulatorarm64)
            }
        }
        val iosMain by creating {
            dependencies {
                implementation(Square.okio)
            }
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
    namespace = "org.rhasspy.mobile.platformspecific"
    buildTypes {
        release {
            buildConfigField("boolean", "IS_DEBUG", "true")
        }
        debug {
            buildConfigField("boolean", "IS_DEBUG", "true")
        }
    }
}