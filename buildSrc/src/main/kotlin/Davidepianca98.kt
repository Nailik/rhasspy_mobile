import de.fayard.refreshVersions.core.DependencyGroup

object Davidepianca98 : DependencyGroup(group = "com.github.davidepianca98") {

    object KMQTT : DependencyGroup(group = group) {
        val common = module("kmqtt-common")
        val client = module("kmqtt-client")
    }

}