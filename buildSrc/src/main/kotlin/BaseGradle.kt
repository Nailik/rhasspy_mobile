@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class BaseGradle : Plugin<Project> {
    override fun apply(project: Project) {

        try {
            val kotlinMultiplatformExtension = project.extensions.getByName("kotlin")
            if (kotlinMultiplatformExtension is KotlinMultiplatformExtension) {
                kotlinMultiplatformExtension.apply {
                    androidTarget()
                    iosX64()
                    iosArm64()
                    iosSimulatorArm64()

                    sourceSets.all {
                        //Warning: This class can only be used with the compiler argument '-opt-in=kotlin.RequiresOptIn'
                        languageSettings.optIn("kotlin.RequiresOptIn")
                    }
                }
            }

        } catch (_: UnknownDomainObjectException) {
        }

        // Configure common android build parameters.
        val androidExtension = project.extensions.getByName("android")
        if (androidExtension is BaseExtension) {
            androidExtension.apply {
                setCompileSdkVersion(33)
                defaultConfig {
                    minSdk = 23
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                composeOptions {
                    kotlinCompilerExtensionVersion = "_"
                }
                buildFeatures.buildConfig = true
            }
        }
    }
}