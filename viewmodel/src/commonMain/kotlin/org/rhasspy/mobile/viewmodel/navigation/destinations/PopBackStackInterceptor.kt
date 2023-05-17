package org.rhasspy.mobile.viewmodel.navigation.destinations

interface PopBackStackInterceptor {

    //returns true when popping is allowed
    fun popBackStack(): Boolean

}