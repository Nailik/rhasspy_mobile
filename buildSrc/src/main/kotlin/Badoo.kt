import de.fayard.refreshVersions.core.DependencyGroup

object Badoo : DependencyGroup(group = "com.badoo.reaktive") {

    val reaktive = module("reaktive")
    val coroutinesInterop = module("coroutines-interop")

}