import de.fayard.refreshVersions.core.DependencyGroup

object Slf4j : DependencyGroup(group = "org.slf4j") {

    val simple = module("slf4j-simple")

}