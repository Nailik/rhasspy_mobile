package org.rhasspy.mobile

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}