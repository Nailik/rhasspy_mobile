import de.fayard.refreshVersions.core.DependencyGroup
import org.gradle.kotlin.dsl.IsNotADependency

object Eclipse : IsNotADependency {

    object Phao : DependencyGroup(group = "org.eclipse.paho") {
        val mqttClient = module("org.eclipse.paho.client.mqttv3")
    }

}