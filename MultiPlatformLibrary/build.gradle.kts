plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
}

version = "0.1"

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "MultiPlatformLibrary"
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
                implementation("co.touchlab:kermit:_")
                implementation(Icerock.Mvvm.core)
                implementation(Icerock.Mvvm.state)
                implementation(Icerock.Mvvm.livedata)
                implementation(Icerock.Mvvm.livedataResources)
                runtimeOnly(Icerock.permissions)
                implementation(Icerock.Resources)
                implementation(Russhwolf.multiplatformSettings)
                implementation(Russhwolf.multiplatformSettingsNoArg)
                implementation(Jetbrains.Kotlinx.dateTime)
                implementation(Jetbrains.Kotlinx.serialization)
                implementation(Ktor.Client.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.Compose.foundation)
                implementation(AndroidX.multidex)
                implementation(AndroidX.window)
                implementation(AndroidX.activity)
                implementation(AndroidX.Compose.ui)
                implementation(AndroidX.Compose.material3)
                implementation(Icerock.Resources.resourcesCompose)
                implementation(Picovoice.porcupineAndroid)
                implementation(Ktor.Server.core)
                implementation(Ktor.Server.netty)
                implementation(Slf4j.simple)
                implementation(Eclipse.Phao.mqttClient)
            }
        }
        val androidTest by getting
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

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
        targetSdk = 32
    }
    packagingOptions {
        resources.pickFirsts.add("META-INF/*")
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "org.rhasspy.mobile" // required
}