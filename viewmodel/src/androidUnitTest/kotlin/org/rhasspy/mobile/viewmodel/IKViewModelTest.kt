package org.rhasspy.mobile.viewmodel

import io.mockk.impl.annotations.RelaxedMockK
import org.koin.core.module.Module
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.viewmodel.navigation.Navigator

abstract class IKViewModelTest : ISettingsTest() {

    @RelaxedMockK
    lateinit var navigator: Navigator

    @RelaxedMockK
    lateinit var nativeApplication: NativeApplication

    @RelaxedMockK
    lateinit var microphonePermission: MicrophonePermission

    @RelaxedMockK
    lateinit var overlayPermission: OverlayPermission

    override fun initModules(): MutableList<Module> {
        return mutableListOf(
            module {
                single { navigator }
                single { nativeApplication }
                single { microphonePermission }
                single { overlayPermission }
            }
        ).apply {
            addAll(
                super.initModules()
            )
        }
    }

}