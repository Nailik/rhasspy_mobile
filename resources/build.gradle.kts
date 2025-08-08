@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
import com.mikepenz.aboutlibraries.plugin.DuplicateRule.SIMPLE
import groovy.json.JsonSlurper
import org.apache.tools.ant.taskdefs.condition.Os
import java.net.URLEncoder

plugins {
    id("base.kmp.compose.library")
    id("dev.icerock.mobile.multiplatform-resources")
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.download)
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
            baseName = "resources"
            isStatic = true
            export(libs.moko.resources)
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        commonMain.dependencies {
            api(libs.moko.resources)
            api(libs.moko.resources.compose)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("org.rhasspy.mobile.resources")
}

aboutLibraries {
    export.prettyPrint = true
    android.registerAndroidTasks = false
    library {
        // Enable the duplication mode, allows to merge, or link dependencies which relate
        duplicationMode = MERGE
        // Configure the duplication rule, to match "duplicates" with
        duplicationRule = SIMPLE
    }
}

android {
    namespace = "org.rhasspy.mobile.resources"
    sourceSets["main"].res.srcDirs("build/generated/assets/generateMRandroidMain")
}

buildkonfig {
    packageName = "org.rhasspy.mobile"
    objectName = "BuildKonfig"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.INT, "versionCode", Version.code.toString())
        buildConfigField(FieldSpec.Type.STRING, "versionName", Version.toString())
    }
}

val exportMyLibraryDefinitions by tasks.registering(Exec::class) {
    group = "build setup"
    description = "Exports library definitions before build"

    // Set working directory
    workingDir = if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        File("$projectDir/..")
    } else {
        file("..")
    }

    // Set command line
    commandLine = listOf(
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            "gradlew.bat"
        } else {
            "./gradlew"
        },
        "exportLibraryDefinitions",
        "-PaboutLibraries.exportPath=${projectDir}/src/commonMain/moko-resources/MR/files"
    )
}

tasks.named("preBuild") {
    dependsOn(exportMyLibraryDefinitions)
}

@Suppress("UNCHECKED_CAST")
//manually run this task to download all models and update keywords
tasks.register("updatePorcupineFiles") {
    doLast {
        val baseUrl = "https://api.github.com/repos/Picovoice/porcupine/contents"
        val baseDest = "$projectDir/src/commonMain/moko-resources/files"

        var src = "$baseUrl/lib/common"
        val contentsFile = layout.buildDirectory.file("directory_contents.json").get().asFile
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
            dest("$baseDest")
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

                fun encodeFilenameInUrl(url: String) = url.substringBeforeLast("/") + "/" +
                        URLEncoder.encode(url.substringAfterLast("/"), "UTF-8")
                            .replace("%2520", "%20")

                // parse directory listing
                contents = JsonSlurper().parse(contentsFile) as List<Map<Any, String>>
                urls = contents.map { url -> encodeFilenameInUrl(url["download_url"]!!) }

                // download files
                download.run {
                    src(urls)
                    dest(baseDest)
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