@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

plugins {
    id("base.kmp.compose.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":data"))
            implementation(project(":resources"))
            implementation(libs.okio)
            implementation(libs.koin.core)
            implementation(libs.moko.resources)
            implementation(libs.kermit)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.network)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.cors)
            implementation(libs.ktor.server.cio)
            implementation(libs.ktor.server.data.conversion)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.server.status.pages)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.okio)
            implementation(libs.okio.jvm)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.multidex)
            implementation(libs.androidx.lifecycle.process)
            implementation(libs.ktor.server.compression)
            implementation(libs.ktor.server.call.logging)
            implementation(libs.ktor.server.cio)
            implementation(libs.koin.android)
            implementation(libs.koin.android.compat)
            implementation(libs.kotlinx.datetime)
            implementation(libs.porcupine)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation("com.google.firebase:firebase-crashlytics")
            implementation("com.google.firebase:firebase-analytics")
            implementation(libs.androidresampler)
            implementation(libs.zxing.android.embedded)
            implementation(libs.androidx.browser)
            implementation(files("libs/org.eclipse.paho.client.mqttv3-1.2.5.jar"))
        }

        androidUnitTest.dependencies {
            implementation(libs.androidx.arch.core.testing)
        }

        iosMain.dependencies {
            implementation(libs.okio)
        }
    }
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