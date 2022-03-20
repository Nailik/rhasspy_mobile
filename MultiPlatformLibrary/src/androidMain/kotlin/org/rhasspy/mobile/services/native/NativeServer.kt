package org.rhasspy.mobile.services.native

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.rhasspy.mobile.services.http.HttpCallWrapper

actual class NativeServer(private val applicationEngine: ApplicationEngine) {

    actual fun start() {
        applicationEngine.start()
    }

    actual fun stop() {
        applicationEngine.stop(0, 0)
    }

    actual companion object {
        actual fun getServer(routing: List<HttpCallWrapper>): NativeServer {

            return NativeServer(embeddedServer(factory = Netty, port = 12101, watchPaths = emptyList()) {
                //install(WebSockets)
                install(CallLogging)
                install(DataConversion)
                // configures Cross-Origin Resource Sharing. CORS is needed to make calls from arbitrary
                // JavaScript clients, and helps us prevent issues down the line.
                install(CORS) {
                    method(HttpMethod.Get)
                    method(HttpMethod.Post)
                    method(HttpMethod.Delete)
                    anyHost()
                }
                // Greatly reduces the amount of data that's needed to be sent to the client by
                // gzipping outgoing content when applicable.
                install(Compression) {
                    gzip()
                }
                routing {

                    routing.forEach { route ->
                        route(route.route, HttpMethod.parse(route.method.name)) {
                            handle {
                                route.body(NativeCall().initialize(call))
                            }
                        }
                    }

                    get("/") {
                        call.respondText("working")
                    }
                }
            })
        }
    }

}

