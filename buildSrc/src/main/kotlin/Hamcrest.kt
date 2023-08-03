import de.fayard.refreshVersions.core.DependencyGroup

object Hamcrest : DependencyGroup(group = "org.hamcrest") {

    val hamcrest = module("hamcrest")

}