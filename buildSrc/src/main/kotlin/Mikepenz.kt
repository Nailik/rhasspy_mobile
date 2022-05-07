import de.fayard.refreshVersions.core.DependencyGroup

object Mikepenz : DependencyGroup(group = "com.mikepenz") {

    val aboutLibrariesCore = module("aboutlibraries-core")
    val aboutLibrariesCompose = module("aboutlibraries-compose")

}