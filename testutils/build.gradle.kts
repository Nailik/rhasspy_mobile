@file:Suppress("UnstableApiUsage")

plugins {
    id("org.kodein.mock.mockmp")
    kotlin("multiplatform")
    id("com.android.library")
    id("base-gradle")
    id("org.gradle.test-retry")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":platformspecific"))
                api(project(":settings"))
                api(project(":logic"))
                api(project(":app"))
                api(project(":viewmodel"))
                implementation(Kotlin.test)
                implementation(Koin.test)
                implementation(KotlinX.Coroutines.test)
                implementation("org.kodein.mock:mockmp-test-helper:_")
                implementation(Russhwolf.multiplatformSettingsTest)
                implementation(Russhwolf.multiplatformSettingsNoArg)
            }
        }
        val commonTest by getting
        val androidMain by getting {
            dependencies {
                implementation(Cashapp.Sqldelight.androidTest)
                implementation(AndroidX.archCore.testing)
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
    namespace = "org.rhasspy.mobile.testutils"

    testOptions {
        unitTests.isReturnDefaultValues = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
}

mockmp {
    usesHelper = true
    public = true
}
mockmp.installWorkaround()

tasks.withType<Test>().configureEach {
    retry {
        maxRetries.set(2)
        maxFailures.set(20)
        failOnPassedAfterRetry.set(false)
    }
}