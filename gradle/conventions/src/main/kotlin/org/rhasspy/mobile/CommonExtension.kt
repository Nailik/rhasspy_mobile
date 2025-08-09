package org.rhasspy.mobile

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal inline fun <reified T : CommonExtension<*, *, *, *, *, *>> Project.configureAndroidCommon() {
    configure<T> {
        compileSdk = 36

        defaultConfig {
            minSdk = 23
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_22
            targetCompatibility = JavaVersion.VERSION_22
        }

        buildFeatures {
            buildConfig = true
        }

        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_22)
            }
        }
    }
}

internal fun Project.configureKotlinCommon() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xexpect-actual-classes",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
                "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                "-P=plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${layout.buildDirectory.get().asFile.absolutePath}/compose_metrics",
                "-P=plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${layout.buildDirectory.get().asFile.absolutePath}/compose_metrics",
                "-opt-in=co.touchlab.kermit.ExperimentalKermitApi",
            )
        }
    }
}
