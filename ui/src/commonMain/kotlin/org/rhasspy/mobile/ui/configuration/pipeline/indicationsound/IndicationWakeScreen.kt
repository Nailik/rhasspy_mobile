package org.rhasspy.mobile.ui.configuration.pipeline.indicationsound

import androidx.compose.runtime.Composable
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationViewModel
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.PipelineConfigurationLocalIndicationSoundDestination.WakeIndicationSoundScreen

/**
 * indication sounds
 */

@Composable
fun IndicationWakeScreen(viewModel: IndicationSoundConfigurationViewModel) {
    IndicationSoundScreen(
        viewModel = viewModel,
        screen = WakeIndicationSoundScreen,
        title = MR.strings.wakeSound.stable
    )
}
