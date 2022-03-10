import de.fayard.refreshVersions.core.DependencyGroup
import org.gradle.kotlin.dsl.IsNotADependency

object Jetbrains : IsNotADependency {

    object Kotlinx : DependencyGroup(group = "org.jetbrains.kotlinx") {
        val dateTime = module("kotlinx-datetime")
        val serialization = module("kotlinx-serialization-json")
    }

}