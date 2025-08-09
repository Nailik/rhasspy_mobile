import com.android.build.gradle.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.rhasspy.mobile.configureAndroidCommon
import org.rhasspy.mobile.configureKotlinCommon

apply(plugin = "com.android.library")
apply(plugin = "org.jetbrains.kotlin.multiplatform")
configureAndroidCommon<LibraryExtension>()
configureKotlinCommon()
configure<KotlinMultiplatformExtension> {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    applyDefaultHierarchyTemplate()
    sourceSets.all {
        //Warning: This class can only be used with the compiler argument '-opt-in=kotlin.RequiresOptIn'
        languageSettings.optIn("kotlin.RequiresOptIn")
    }
}