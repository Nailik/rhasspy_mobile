import de.fayard.refreshVersions.core.DependencyGroup

object Russhwolf : DependencyGroup(group = "com.russhwolf") {

    val multiplatformSettings = module("multiplatform-settings")
    val multiplatformSettingsTest = module("multiplatform-settings-test")
    val multiplatformSettingsNoArg = module("multiplatform-settings-no-arg")
    val multiplatformSettingsSerialization = module("multiplatform-settings-serialization")

}