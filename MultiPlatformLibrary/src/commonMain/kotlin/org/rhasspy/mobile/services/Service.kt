package org.rhasspy.mobile.services

expect abstract class Service() {

    abstract fun startServices()

    abstract fun stopServices()

}