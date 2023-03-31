@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.codingfeline.buildkonfig")
}

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Icerock.Resources)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
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
    multiplatformResourcesPackage = "org.rhasspy.mobile" // required
}

android {
    namespace = "org.rhasspy.mobile.resources"
    compileSdk = 33
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    if (org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC)) {
        sourceSets.getByName("main").res.srcDir(File(buildDir, "generated/moko/androidMain/res"))
    }
}

buildkonfig {
    packageName = "org.rhasspy.mobile"
    objectName = "BuildKonfig"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "changelog", generateChangelog())
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT, "versionCode", Version.code.toString())
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "versionName", Version.toString())
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