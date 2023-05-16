package org.rhasspy.mobile.viewmodel.navigation.destinations.configuration

import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.ConfigurationScreenDestinationType.Edit
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.ConfigurationScreenDestinationType.Test

enum class DialogManagementConfigurationScreenDestination(val destinationType: ConfigurationScreenDestinationType) : NavigationDestination {

    EditScreen(Edit),
    TestScreen(Test)

}