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

        composeOptions {
            kotlinCompilerExtensionVersion = "_"
        }
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_22)
            }
        }
    }
}