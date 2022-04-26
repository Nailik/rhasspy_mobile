import de.fayard.refreshVersions.core.DependencyGroup

object Russhwolf : DependencyGroup(group = "com.russhwolf") {

    val multiplatformSettings = module("multiplatform-settings")
    val multiplatformSettingsNoArg = module("multiplatform-settings-no-arg")
    val multiplatformSettingsSerialization = module("multiplatform-settings-serialization")

}