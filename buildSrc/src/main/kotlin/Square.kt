import de.fayard.refreshVersions.core.DependencyGroup
import de.fayard.refreshVersions.core.DependencyNotationAndGroup
import org.gradle.kotlin.dsl.IsNotADependency

object Square : DependencyGroup(group = "com.squareup") {

    val okio = module("okio")

    object Okio : DependencyGroup(group = "com.squareup.okio") {
        val jvm = module("okio-jvm")
        val iosx64 = module("okio-iosx64")
        val iosarm64 = module("okio-iosarm64")
        val iossimulatorarm64 = module("okio-iossimulatorarm64")
    }

}