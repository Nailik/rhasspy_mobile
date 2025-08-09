@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

plugins {
    id("base.kmp.compose.library")
    alias(libs.plugins.mockmp)
    alias(libs.plugins.ksp)
    alias(libs.plugins.test.retry)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":data"))
            implementation(project(":platformspecific"))
            implementation(project(":logic"))
            implementation(project(":resources"))
            implementation(project(":settings"))
            implementation(libs.kermit)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.datetime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.koin.core)
            implementation(libs.moko.mvvm.core)
            implementation(libs.ktor.client.core)
            implementation(libs.moko.resources.compose)
            implementation(libs.okio)
            implementation(libs.aboutlibraries.core)
            implementation(libs.multiplatform.settings.no.arg)
        }
        commonTest.dependencies {
            implementation(project(":app"))
            implementation(libs.koin.test)
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.multiplatform.settings.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.multidex)
            implementation(libs.okio)
        }
        androidUnitTest.dependencies {
            implementation(libs.androidx.arch.core.testing)
        }

    }
}

android {
    namespace = "org.rhasspy.mobile.viewmodel"

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
        ignoreFailures = true
        maxRetries.set(20)
        maxFailures.set(20)
        failOnPassedAfterRetry.set(false)
    }
}
