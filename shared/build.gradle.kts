@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.mikepenz.aboutlibraries.plugin")
    id("org.sonarqube")
    id("org.jetbrains.compose")
    id("co.touchlab.crashkios.crashlyticslink")
}

version = Version.toString()

kotlin {
    targets {
        android()
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "16.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
    }


    sourceSets {
        all {
            //Warning: This class can only be used with the compiler argument '-opt-in=kotlin.RequiresOptIn'
            languageSettings.optIn("kotlin.RequiresOptIn")

        }

        val commonMain by getting {
            dependencies {
                implementation(project(":logic"))
                implementation(project(":ui"))
                implementation(project(":viewmodel"))
                implementation(project(":platformspecific"))
                implementation(project(":data"))
                implementation(Kotlin.Stdlib.common)
                implementation(Touchlab.kermit)
                implementation(Touchlab.Kermit.crashlytics)
                implementation(Icerock.Mvvm.core)
                implementation(Icerock.Resources)
                implementation(Jetbrains.Kotlinx.dateTime)
                implementation(Jetbrains.Kotlinx.serialization)
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
                implementation(Jetbrains.Compose.ui)
                implementation(Jetbrains.Compose.foundation)
                implementation(Jetbrains.Compose.material3)
                implementation(Jetbrains.Compose.runtime)
                implementation(Square.okio)
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
                implementation(AndroidX.Compose.foundation)
                implementation(AndroidX.multidex)
                implementation(AndroidX.window)
                implementation(AndroidX.activity)
                implementation(AndroidX.documentFile)
                implementation(AndroidX.Compose.ui)
                implementation(AndroidX.Compose.material3)
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
                implementation(Kotlin.Test.junit)
                implementation(Kotlin.test)
                implementation(Kotlin.Test.junit)
                implementation(AndroidX.Compose.Ui.testJunit4)
                implementation(AndroidX.Compose.Ui.testManifest)
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

    kotlinOptions {
        jvmTarget = "1.8"
    }
}


android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 23
    }
    packagingOptions {
        resources.pickFirsts.add("META-INF/*")
        resources.pickFirsts.add("BuildConfig.kt")
        resources.pickFirsts.add("BuildConfig.dex")
    }
    namespace = "org.rhasspy.mobile"
}

aboutLibraries {
    registerAndroidTasks = true
    // Enable the duplication mode, allows to merge, or link dependencies which relate
    duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
    // Configure the duplication rule, to match "duplicates" with
    duplicationRule = com.mikepenz.aboutlibraries.plugin.DuplicateRule.SIMPLE
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

sonarqube {
    properties {
        property("sonar.projectKey", "Nailik_rhasspy_mobile")
        property("sonar.organization", "nailik")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sources", "src,../androidApp/src")
        property("sonar.verbose", "true")
    }
}

val createVersionTxt = tasks.register("createVersionTxt") {
    doLast {
        File(projectDir.parent, "version").also {
            it.writeText("V_$Version")
        }
    }
}
tasks.findByPath("preBuild")!!.dependsOn(createVersionTxt)

val increaseCodeVersion = tasks.register("increaseCodeVersion") {
    doLast {
        File(projectDir.parent, "buildSrc/src/main/kotlin/Version.kt").also {
            it.writeText(
                it.readText().replace("code = ${Version.code}", "code = ${Version.code + 1}")
            )
        }
    }
}

compose {
    //necessary to use the androidx compose compiler for multiplatform in order to use kotlin 1.8
    kotlinCompilerPlugin.set(AndroidX.Compose.compiler.toString())
}