import de.fayard.refreshVersions.core.DependencyGroup
import de.fayard.refreshVersions.core.DependencyNotationAndGroup

object Picovoice : DependencyGroup(group = "ai.picovoice") {

    val porcupineAndroid = module("porcupine-android")

}