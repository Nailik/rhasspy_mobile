@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("org.jetbrains.compose")
}

version = Version.toString()

kotlin {
    ios()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "iosApp"
            isStatic = true
            transitiveExport = true
            freeCompilerArgs += listOf(
                "-linker-option", "-framework", "-linker-option", "Metal",
                "-linker-option", "-framework", "-linker-option", "CoreText",
                "-linker-option", "-framework", "-linker-option", "CoreGraphics",
                "-Xdisable-phases=VerifyBitcode"
            )
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries.withType<Framework> {
            export(Icerock.Resources.resourcesCompose)
            export(Touchlab.kermit)
            export(project(":shared"))
            export(project(":ui"))
        }
    }

    sourceSets {
        val iosMain by getting {
            dependencies {
                implementation(project(":ui"))
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
    }

}