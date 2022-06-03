import de.fayard.refreshVersions.core.DependencyGroup

object Ktor2 : DependencyGroup(group = "io.ktor") {

    object Server : DependencyGroup(group = group) {
        val core = module("ktor-server-core")
        val cors = module("ktor-server-cors")
        val dataConversion = module("ktor-server-data-conversion")
        val cio = module("ktor-server-cio")
        val compression = module("ktor-server-compression")
        val callLogging = module("ktor-server-call-logging")
    }

    object Client : DependencyGroup(group = group) {
        val cio = module("ktor-client-cio")
    }

}