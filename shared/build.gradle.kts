@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("co.touchlab.crashkios.crashlyticslink")
    id("com.google.devtools.ksp")
    id("base-gradle")
}

version = Version.toString()

kotlin {

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":ui"))
                implementation(project(":data"))
                implementation(project(":logic"))
                implementation(project(":viewmodel"))
                implementation(project(":platformspecific"))
                implementation(project(":settings"))
                implementation(Kotlin.Stdlib.common)
                implementation(Touchlab.kermit)
                implementation(Touchlab.Kermit.crashlytics)
                implementation(Icerock.Mvvm.core)
                implementation(Icerock.Resources.resourcesCompose)
                implementation(Jetbrains.Kotlinx.dateTime)
                implementation(Jetbrains.Kotlinx.serialization)
                implementation(Jetbrains.Kotlinx.immutable)
                implementation(Ktor.Client.core)
                implementation(Ktor.plugins.network)
                implementation(Ktor2.Server.core)
                implementation(Ktor2.Server.cors)
                implementation(Ktor2.Server.cio)
                implementation(Ktor2.Server.dataConversion)
                implementation(Ktor2.Client.cio)
                implementation(Ktor.Server.statusPages)
                implementation(Ktor.Plugins.network)
                implementation(Benasher.uuid)
                implementation(Koin.core)
                implementation(Square.okio)
                implementation(Russhwolf.multiplatformSettingsNoArg)
            }
        }
        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.appCompat)
                implementation(AndroidX.lifecycle.process)
                implementation(AndroidX.Fragment.ktx)
                implementation(AndroidX.multidex)
                implementation(AndroidX.window)
                implementation(AndroidX.activity)
                implementation(AndroidX.documentFile)
                implementation(Icerock.Resources.resourcesCompose)
                implementation(Slf4j.simple)
                implementation(Ktor2.Server.compression)
                implementation(Ktor2.Server.callLogging)
                implementation(Ktor.Server.netty)
                implementation(Ktor.Plugins.networkTlsCertificates)
            }
        }
        val androidUnitTest by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Kotlin.test)
                implementation(Kotlin.Test.junit)
            }
        }
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

    afterEvaluate {
        // Remove log pollution until Android support in KMP improves.
        project.extensions.findByType<KotlinMultiplatformExtension>()?.let { kmpExt ->
            kmpExt.sourceSets.removeAll {
                setOf(
                    "androidTestFixtures",
                    "androidTestFixturesDebug",
                    "androidTestFixturesRelease",
                ).contains(it.name)
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-opt-in=co.touchlab.kermit.ExperimentalKermitApi"
}

android {
    namespace = "org.rhasspy.mobile.shared"
}

tasks.withType<Test> {
    testLogging {
        events(STARTED, PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR)
        exceptionFormat = FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

val createVersionTxt: TaskProvider<Task> = tasks.register("createVersionTxt") {
    doLast {
        File(projectDir.parent, "version").also {
            it.writeText("V_$Version")
        }
    }
}

tasks.findByPath("preBuild")!!.dependsOn(createVersionTxt)

val increaseCodeVersion: TaskProvider<Task> = tasks.register("increaseCodeVersion") {
    doLast {
        File(projectDir.parent, "buildSrc/src/main/kotlin/Version.kt").also {
            it.writeText(
                it.readText().replace("code = ${Version.code}", "code = ${Version.code + 1}")
            )
        }
    }
}