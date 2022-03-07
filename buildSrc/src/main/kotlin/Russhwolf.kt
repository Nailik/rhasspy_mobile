import de.fayard.refreshVersions.core.DependencyGroup
import de.fayard.refreshVersions.core.DependencyNotationAndGroup

object Russhwolf : DependencyGroup(group = "com.russhwolf") {

    val multiplatformSettings = module("multiplatform-settings")
    val multiplatformSettingsNoArg = module("multiplatform-settings-no-arg")

}