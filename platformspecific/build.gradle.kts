@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("base-gradle")
    id("app.cash.sqldelight")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":data"))
                implementation(project(":resources"))
                implementation(Square.okio)
                implementation(Koin.core)
                implementation(Icerock.Resources.resourcesCompose)
                implementation(Touchlab.kermit)
                implementation(Jetbrains.Kotlinx.serialization)
                implementation(Jetbrains.Kotlinx.immutable)
                implementation(Jetbrains.Kotlinx.coroutines)
                implementation(Ktor3.Server.core)
                implementation(Ktor3.Server.cors)
                implementation(Ktor3.Server.cio)
                implementation(Ktor3.Server.dataConversion)
                implementation(Ktor3.Server.statusPages)
                implementation(Ktor3.Client.core)
                implementation(Ktor3.Client.cio)
                implementation(Ktor3.Client.logging)
                implementation(Ktor3.Plugins.network)
                api(Cashapp.Paging.runtime)
                api(Cashapp.Sqldelight.paging)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                api(AndroidX.multidex)
                implementation(Square.okio)
                implementation(AndroidX.appCompat)
                implementation(AndroidX.lifecycle.process)
                implementation(AndroidX.appCompat)
                implementation(Ktor3.Server.compression)
                implementation(Ktor3.Server.callLogging)
                implementation(Ktor3.Server.netty)
                implementation(Ktor3.Client.android)
                implementation(Ktor3.Client.okHttp)
                implementation(Koin.android)
                implementation(Koin.androidCompat)
                implementation(Jetbrains.Kotlinx.dateTime)
                implementation(Picovoice.porcupineAndroid)
                implementation(Firebase.analyticsKtx)
                implementation(Firebase.crashlyticsKtx)
                implementation(project.dependencies.platform(Firebase.bom))
                implementation(Nailik.androidResampler)
                implementation(Journeyapps.zXingAndroid)
                implementation(AndroidX.browser)
                implementation(Cashapp.Sqldelight.android)
                api(Requery.sqliteAndroid)
                implementation(files("libs/org.eclipse.paho.client.mqttv3-1.2.5.jar"))
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(AndroidX.archCore.testing)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies {
                implementation(Kotlin.Stdlib.common)
                implementation(Square.okio)
                implementation(Cashapp.Sqldelight.ios)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xexpect-actual-classes"
}

android {
    namespace = "org.rhasspy.mobile.platformspecific"
    buildTypes {
        release {
            buildConfigField("boolean", "IS_DEBUG", "false")
        }
        debug {
            buildConfigField("boolean", "IS_DEBUG", "true")
        }
    }
}

sqldelight {
    databases {
        create("LogDatabase") {
            dialect("app.cash.sqldelight:sqlite-3-30-dialect:_")
            packageName.set("org.rhasspy.mobile.logging")
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
            srcDirs("src/commonMain/sqldelight/org/rhasspy/mobile/logging")
            verifyMigrations.set(true)
        }
    }
}