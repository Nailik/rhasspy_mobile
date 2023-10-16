import de.fayard.refreshVersions.core.DependencyGroup
import org.gradle.kotlin.dsl.IsNotADependency

object Ktor2 : DependencyGroup(group = "io.ktor") {

    object Plugins : IsNotADependency {
        val network = module("ktor-network")
    }

    object Server : DependencyGroup(group = group) {
        val core = module("ktor-server-core")
        val cors = module("ktor-server-cors")
        val dataConversion = module("ktor-server-data-conversion")
        val cio = module("ktor-server-cio")
        val compression = module("ktor-server-compression")
        val callLogging = module("ktor-server-call-logging")
        val statusPages = module("ktor-server-status-pages")
        val netty = module("ktor-server-netty")
    }

    object Client : DependencyGroup(group = group) {
        val cio = module("ktor-client-cio")
        val core = module("ktor-client-core")
        val websockets = module("ktor-client-websockets")
    }

}