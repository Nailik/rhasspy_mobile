import de.fayard.refreshVersions.core.DependencyGroup
import de.fayard.refreshVersions.core.DependencyNotationAndGroup

object Icerock : DependencyGroup(group = "dev.icerock.moko") {

    val permissions = module("permissions")

    object Mvvm : DependencyGroup(group = group) {
        val core = module("mvvm-core")
        val state = module("mvvm-state")
        val livedata = module("mvvm-livedata")
        val livedataResources = module("mvvm-livedata-resources")
    }

    object Resources : DependencyNotationAndGroup(group = group, name = "resources") {
        val generator = module("resources-generator")
        val resourcesCompose = module("resources-compose")
    }

}