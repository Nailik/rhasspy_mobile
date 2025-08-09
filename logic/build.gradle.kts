@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

plugins {
    id("base.kmp.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.mockmp)
    alias(libs.plugins.test.retry)
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":data"))
            implementation(project(":platformspecific"))
            implementation(project(":resources"))
            implementation(project(":settings"))
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kermit)
            implementation(libs.moko.resources)
            implementation(libs.ktor.network)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.cors)
            implementation(libs.ktor.server.cio)
            implementation(libs.ktor.server.data.conversion)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.server.status.pages)
            implementation(libs.benasher44.uuid)
            implementation(libs.okio)
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(project(":platformspecific"))
            implementation(libs.multiplatform.settings.test)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.kotlin.test)
            implementation(libs.koin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.multidex)
            implementation(libs.porcupine)
        }
        androidUnitTest.dependencies {
            implementation(project(":androidApp"))
            implementation(project(":app"))
            implementation(libs.androidx.arch.core.testing)
        }
    }
}

android {
    namespace = "org.rhasspy.mobile.logic"

    testOptions {
        unitTests.isReturnDefaultValues = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
}

mockmp {
    onTest {
        withHelper()
    }
}

tasks.withType<Test>().configureEach {
    retry {
        maxRetries.set(2)
        maxFailures.set(20)
        failOnPassedAfterRetry.set(false)
    }
}