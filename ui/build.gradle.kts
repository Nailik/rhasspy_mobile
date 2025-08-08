@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("base.kmp.compose.library")
}

version = Version.toString()

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":viewmodel"))
            implementation(project(":data"))
            implementation(project(":logic"))
            implementation(project(":resources"))
            implementation(project(":settings"))
            implementation(project(":platformspecific"))
            implementation(libs.moko.resources.compose)
            implementation(libs.moko.mvvm.core)
            implementation(libs.koin.core)
            implementation(libs.kermit)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.aboutlibraries.core)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            // Deprecated in favor of Activity.enableEdgeToEdge from androidx.activity 1.8+
            //FIXME: See the example PR in the migration guide here:
            // https://google.github.io/accompanist/systemuicontroller/
            implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.multidex)
        }
    }

}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-P=plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_metrics",
            "-P=plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_metrics"
        )
    }
}

android {
    namespace = "org.rhasspy.mobile.ui"
    buildFeatures {
        compose = true
    }
}
dependencies {
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.3")
}
