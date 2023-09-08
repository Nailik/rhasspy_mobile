package org.rhasspy.mobile.android.settings.sound

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.IndicationSettingsScreenDestination
import org.rhasspy.mobile.viewmodel.settings.indication.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsViewModel

class ErrorIndicationSoundScreenTest : KoinComponent, IndicationSoundScreenTest(
    title = MR.strings.errorSound.stable,
    screen = IndicationSettingsScreenDestination.ErrorIndicationSoundScreen
) {

    override val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    override fun getViewModelInstance(): IIndicationSoundSettingsViewModel =
        get<ErrorIndicationSoundSettingsViewModel>()

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    override fun testAddSoundFile() {
        super.testAddSoundFile()
    }

    @Test
    override fun testPlayback() {
        super.testPlayback()
    }

}