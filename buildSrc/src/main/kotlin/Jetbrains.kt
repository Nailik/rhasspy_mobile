import de.fayard.refreshVersions.core.DependencyGroup
import org.gradle.kotlin.dsl.IsNotADependency

object Jetbrains : IsNotADependency {

    object Kotlinx : DependencyGroup(group = "org.jetbrains.kotlinx") {
        val dateTime = module("kotlinx-datetime")
        val serialization = module("kotlinx-serialization-json")
        val coroutines = module("kotlinx-coroutines-core")
        val immutable = module("kotlinx-collections-immutable")
        val atomicfu = module("atomicfu")
    }

    object Compose : DependencyGroup(group = "org.jetbrains.compose") {
        val ui = module("$group.ui", "ui")
        val full = module("$group", "compose-full")
        val preview = module("$group.ui", "ui-tooling-preview")
        val testJunit4 = module("$group.ui", "ui-test-junit4")
        val foundation = module("$group.foundation", "foundation")
        val material = module("$group.material", "material")
        val material3 = module("$group.material3", "material3")
        val runtime = module("$group.runtime", "runtime")
        val materialIconsExtended = module("$group.material", "material-icons-extended")
    }
}