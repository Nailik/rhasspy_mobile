@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":data"))
                implementation(Square.okio)
                implementation(Ktor2.Client.core)
                implementation(Koin.core)
                implementation(Icerock.Resources)
                implementation(Touchlab.kermit)
                implementation(Jetbrains.Kotlinx.serialization)
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
                implementation(AndroidX.appCompat)
                implementation(AndroidX.multidex)
                implementation(AndroidX.lifecycle.process)
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
    compileSdk = 33
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
}