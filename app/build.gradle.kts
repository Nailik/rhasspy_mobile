@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE", "UNNECESSARY_NOT_NULL_ASSERTION")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT
import org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("base.kmp.compose.library")
    alias(libs.plugins.crashlyticslink) apply false
    alias(libs.plugins.native.cocoapods)
}

version = Version.toString()

kotlin {

    cocoapods {
        summary = "Some description for the app Module"
        homepage = "Link to the app Module homepage"
        ios.deploymentTarget = "14.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "app"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":ui"))
            implementation(project(":data"))
            implementation(project(":logic"))
            implementation(project(":viewmodel"))
            implementation(project(":platformspecific"))
            implementation(project(":settings"))
            implementation(project(":overlay"))
            implementation(project(":widget"))
            implementation(libs.kotlin.stdlib.common)
            implementation(libs.kermit)
            implementation(libs.kermit.crashlytics)
            implementation(libs.moko.mvvm.core)
            implementation(libs.moko.resources)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.network)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.cors)
            implementation(libs.ktor.server.cio)
            implementation(libs.ktor.server.data.conversion)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.server.status.pages)
            implementation(libs.benasher44.uuid)
            implementation(libs.koin.core)
            implementation(libs.okio)
            implementation(libs.multiplatform.settings.no.arg)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.process)
            implementation(libs.androidx.fragment.ktx)
            implementation(libs.androidx.multidex)
            implementation(libs.androidx.window)
            implementation(libs.androidx.documentfile)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.moko.resources.compose)
            implementation(libs.slf4j.simple)
            implementation(libs.ktor.server.compression)
            implementation(libs.ktor.server.call.logging)
            implementation(libs.ktor.server.netty)
            implementation(libs.ktor.network.tls.certificates)
        }
        androidUnitTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.test.junit)
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

android {
    namespace = "org.rhasspy.mobile.app"
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

@Suppress("unused")
val increaseCodeVersion: TaskProvider<Task> = tasks.register("increaseCodeVersion") {
    doLast {
        File(projectDir.parent, "gradle/conventions/src/main/kotlin/Version.kt").also {
            it.writeText(
                it.readText().replace("code = ${Version.code}", "code = ${Version.code + 1}")
            )
        }
    }
}