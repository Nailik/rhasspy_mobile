@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
import com.mikepenz.aboutlibraries.plugin.DuplicateRule.SIMPLE
import groovy.json.JsonSlurper
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.codingfeline.buildkonfig")
    id("org.jetbrains.compose")
    id("com.mikepenz.aboutlibraries.plugin")
    id("de.undercouch.download")
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
            baseName = "resources"
            isStatic = true
            export(Icerock.Resources)
            export(Touchlab.kermit)
            export(Jetbrains.Compose.full)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Icerock.Resources)
                api(Touchlab.kermit)
                api(Jetbrains.Compose.full)
                implementation(Jetbrains.Kotlinx.atomicfu)
                implementation(Icerock.Resources.resourcesCompose)
                implementation(Jetbrains.Compose.ui)
                implementation(Jetbrains.Compose.foundation)
                implementation(Jetbrains.Compose.material)
                implementation(Jetbrains.Compose.material3)
                implementation(Jetbrains.Compose.runtime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Kotlin.test)
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

multiplatformResources {
    multiplatformResourcesPackage = "org.rhasspy.mobile.resources" // required
    disableStaticFrameworkWarning = true
}

aboutLibraries {
    prettyPrint = true
    registerAndroidTasks = false
    // Enable the duplication mode, allows to merge, or link dependencies which relate
    duplicationMode = MERGE
    // Configure the duplication rule, to match "duplicates" with
    duplicationRule = SIMPLE
}

android {
    namespace = "org.rhasspy.mobile.resources"
    buildFeatures.compose = true
}

buildkonfig {
    packageName = "org.rhasspy.mobile"
    objectName = "BuildKonfig"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "changelog", generateChangelog())
        buildConfigField(FieldSpec.Type.INT, "versionCode", Version.code.toString())
        buildConfigField(FieldSpec.Type.STRING, "versionName", Version.toString())
    }
}

tasks.findByPath("preBuild")!!.doFirst {
    exec {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            workingDir = File("$projectDir/..")
        }
        commandLine = listOf(
            if (Os.isFamily(Os.FAMILY_WINDOWS)) {
                "gradlew.bat"
            } else "../gradlew",
            "exportLibraryDefinitions",
            "-PaboutLibraries.exportPath=${projectDir}/src/commonMain/resources/MR/files"
        )
    }
}

fun generateChangelog(): String {
    try {
        var os = ByteArrayOutputStream()

        exec {
            standardOutput = os
            commandLine = listOf("git")
            args = listOf("describe", "--tags", "--abbrev=0")
        }

        val lastTag = String(os.toByteArray()).trim()
        os.close()

        os = ByteArrayOutputStream()
        exec {
            standardOutput = os
            commandLine = listOf("git")
            args = listOf(
                "log",
                "$lastTag..develop",
                "--merges",
                "--first-parent",
                "--pretty=format:\"%b\"\\\\"
            )
        }
        val changelog = String(os.toByteArray()).trim()
        os.close()

        return changelog
    } catch (e: Exception) {
        return ""
    }
}

@Suppress("UNCHECKED_CAST")
//manually run this task to download all models and update keywords
tasks.register("updatePorcupineFiles") {
    doLast {
        val baseUrl = "https://api.github.com/repos/Picovoice/porcupine/contents"
        val baseDest = "$projectDir/src/commonMain/resources/MR/files/porcupine"

        var src = "$baseUrl/lib/common"
        val contentsFile = File(buildDir, "directory_contents.json")
        download.run {
            src(src)
            dest(contentsFile)
        }

        // parse directory listing
        var contents = JsonSlurper().parse(contentsFile) as List<Map<Any, String>>
        var urls = contents.map { url -> url["download_url"] }

        // download files
        download.run {
            src(urls)
            dest("$baseDest/models")
            overwrite(true)
            onlyIfModified(true)
            eachFile {
                //correctly encode non standard letters
                name = name.replace(Regex("[^A-Za-z0-9._]"), "")
            }
        }

        urls.forEach { modelUrl ->
            if (modelUrl != null) {
                //language contains _ for english it's empty else _de
                val language = modelUrl
                    .replace(Regex(".*/porcupine_params"), "")
                    .replace(".pv", "")

                src = "$baseUrl/resources/keyword_files$language/android"

                download.run {
                    src(src)
                    dest(contentsFile)
                }

                // parse directory listing
                contents = JsonSlurper().parse(contentsFile) as List<Map<Any, String>>
                urls = contents.map { url -> url["download_url"] }

                // download files
                download.run {
                    src(urls)
                    dest("$baseDest/keyword_files$language")
                    overwrite(true)
                    onlyIfModified(true)
                    eachFile {
                        //correctly encode non standard letters
                        name = name.replace(Regex("[^A-Za-z0-9._]"), "")
                    }
                }

            }
        }

    }

}