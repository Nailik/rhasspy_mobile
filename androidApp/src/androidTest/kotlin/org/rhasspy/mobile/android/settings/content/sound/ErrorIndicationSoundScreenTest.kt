package org.rhasspy.mobile.android.settings.content.sound

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Test
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.settings.indication.sound.ErrorIndicationSoundSettingsViewModel

class ErrorIndicationSoundScreenTest : IndicationSoundScreenTest(
    viewModel = ErrorIndicationSoundSettingsViewModel(),
    title = MR.strings.errorSound.stable,
    screen = IndicationSettingsScreens.ErrorIndicationSound
) {


    override val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

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