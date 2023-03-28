plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    targets {
        android()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ui"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                /*  implementation("org.jetbrains.compose.ui:ui:_")
                  implementation("org.jetbrains.compose.foundation:foundation:_")
                  implementation("org.jetbrains.compose.material3:material3:_")
                  implementation("org.jetbrains.compose.runtime:runtime:_") */
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
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
    namespace = "org.rhasspy.mobile.ui"
    compileSdk = 33
    defaultConfig {
        minSdk = 23
    }
}