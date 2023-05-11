import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.sonarqube.gradle.SonarExtension

class BaseGradle : Plugin<Project> {
    override fun apply(project: Project) {
        // Apply Required Plugins.
        project.plugins.apply("org.sonarqube")

        try {
            val kotlinMultiplatformExtension = project.extensions.getByName("kotlin")
            if (kotlinMultiplatformExtension is KotlinMultiplatformExtension) {
                kotlinMultiplatformExtension.apply {
                    android()
                    iosX64()
                    iosArm64()
                    iosSimulatorArm64()

                    sourceSets.all {
                        //Warning: This class can only be used with the compiler argument '-opt-in=kotlin.RequiresOptIn'
                        languageSettings.optIn("kotlin.RequiresOptIn")
                    }
                }
            }

        } catch (e: UnknownDomainObjectException) {
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
                project.tasks.withType(KotlinCompile::class.java).configureEach {
                    kotlinOptions {
                        jvmTarget = "17"
                    }
                }
                composeOptions {
                    kotlinCompilerExtensionVersion = "_"
                }
                buildFeatures.buildConfig = true
            }
        }

        val sonarExtension = project.extensions.getByName("sonarqube")
        if (sonarExtension is SonarExtension) {
            sonarExtension.apply {
                properties {
                    property("sonar.projectKey", "Nailik_rhasspy_mobile")
                    property("sonar.organization", "nailik")
                    property("sonar.host.url", "https://sonarcloud.io")
                    property("sonar.sources", "src")
                    property("sonar.exclusions", "src/androidTest/**")
                }
            }
        }
    }
}