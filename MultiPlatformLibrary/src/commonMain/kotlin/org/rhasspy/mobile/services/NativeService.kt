package org.rhasspy.mobile.services

expect class NativeService() {

    companion object {

        fun doAction(action: Action)

    }

}