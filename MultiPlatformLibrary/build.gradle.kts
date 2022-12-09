@file:Suppress("UnstableApiUsage")

import co.touchlab.faktory.crashlyticsLinkerConfig
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.codingfeline.buildkonfig")
    id("org.sonarqube") version "3.5.0.2730"
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
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "MultiPlatformLibrary"
            isStatic = true
        }
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {

        all {
            //Warning: This class can only be used with the compiler argument '-opt-in=kotlin.RequiresOptIn'
            languageSettings.optIn("kotlin.RequiresOptIn")
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(kotlin("stdlib"))
                implementation(Touchlab.kermit)
                implementation(Touchlab.Kermit.crashlytics)
                implementation(Icerock.Mvvm.core)
                implementation(Icerock.Resources)
                implementation(Russhwolf.multiplatformSettings)
                implementation(Russhwolf.multiplatformSettingsNoArg)
                implementation(Russhwolf.multiplatformSettingsSerialization)
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
                implementation(Picovoice.porcupineAndroid)
                implementation(Slf4j.simple)
                implementation(Ktor2.Server.compression)
                implementation(Ktor2.Server.callLogging)
                implementation(Ktor.Server.netty)
                implementation(Ktor.Plugins.networkTlsCertificates)
                implementation(files("libs/org.eclipse.paho.client.mqttv3-1.2.5.jar"))
            }
        }
        val androidTest by getting {
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

    crashlyticsLinkerConfig()
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
    }
    namespace = "org.rhasspy.mobile"
}

multiplatformResources {
    multiplatformResourcesPackage = "org.rhasspy.mobile" // required
}

aboutLibraries {
    registerAndroidTasks = true
    // Enable the duplication mode, allows to merge, or link dependencies which relate
    duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
    // Configure the duplication rule, to match "duplicates" with
    duplicationRule = com.mikepenz.aboutlibraries.plugin.DuplicateRule.SIMPLE
}

buildkonfig {
    packageName = "org.rhasspy.mobile"
    objectName = "BuildKonfig"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(STRING, "changelog", generateChangelog())
        buildConfigField(INT, "versionCode", Version.code.toString())
        buildConfigField(STRING, "versionName", Version.toString())
    }
}

fun generateChangelog(): String {
    try {
        var os = org.apache.commons.io.output.ByteArrayOutputStream()

        exec {
            standardOutput = os
            commandLine = listOf("git")
            args = listOf("describe", "--tags", "--abbrev=0")
        }

        val lastTag = String(os.toByteArray()).trim()
        os.close()

        os = org.apache.commons.io.output.ByteArrayOutputStream()
        exec {
            standardOutput = os
            commandLine = listOf("git")
            args = listOf("log", "$lastTag..develop", "--merges", "--first-parent", "--pretty=format:\"%b\"\\\\")
        }
        val changelog = String(os.toByteArray()).trim()
        os.close()

        return changelog
    } catch (e: Exception) {
        return ""
    }
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
            it.writeText(it.readText().replace("code = ${Version.code}", "code = ${Version.code + 1}"))
        }
    }
}

tasks.register("increasePatchCodeVersion") {
    doLast {
        File(projectDir.parent, "buildSrc/src/main/kotlin/Version.kt").also {
            it.writeText(it.readText().replace("patch = ${Version.patch}", "patch = ${Version.patch + 1}"))
        }
    }
}

tasks.register("increaseMinorCodeVersion") {
    doLast {
        File(projectDir.parent, "buildSrc/src/main/kotlin/Version.kt").also {
            it.writeText(it.readText().replace("minor = ${Version.minor}", "minor = ${Version.minor + 1}"))
        }
    }
}

tasks.register("increaseMajorCodeVersion") {
    doLast {
        File(projectDir.parent, "buildSrc/src/main/kotlin/Version.kt").also {
            it.writeText(it.readText().replace("major = ${Version.major}", "major = ${Version.major + 1}"))
        }
    }
}