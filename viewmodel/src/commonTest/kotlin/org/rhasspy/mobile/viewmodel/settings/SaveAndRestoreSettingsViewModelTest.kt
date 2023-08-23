package org.rhasspy.mobile.viewmodel.settings

import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.settings.ISettingsUtils
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.coEvery
import org.rhasspy.mobile.testutils.coVerify
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SaveAndRestoreSettingsViewModelTest : AppTest() {

    @Mock
    lateinit var settingsUtils: ISettingsUtils

    private lateinit var saveAndRestoreSettingsViewModel: SaveAndRestoreSettingsViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { settingsUtils }
            }
        )

        saveAndRestoreSettingsViewModel = get()
    }

    @Test
    fun `when the user wants to share a settings file and it returns false a snack bar is shown`() =
        runTest {
            coEvery { settingsUtils.shareSettingsFile() } returns false

            saveAndRestoreSettingsViewModel.onEvent(ShareSettingsFile)

            assertNotNull(saveAndRestoreSettingsViewModel.viewState.value.snackBarText)
        }

    @Test
    fun `when the user wants to share a settings file and it returns true no snack bar is shown`() =
        runTest {
            coEvery { settingsUtils.shareSettingsFile() } returns true

            saveAndRestoreSettingsViewModel.onEvent(ShareSettingsFile)

            assertNull(saveAndRestoreSettingsViewModel.viewState.value.snackBarText)
        }

    @Test
    fun `when a user wants to export the settings file a dialog is shown and the user accepts the dialog the file export is started and when it returns false a dialog is shown`() =
        runTest {
            coEvery { settingsUtils.exportSettingsFile() } returns false

            saveAndRestoreSettingsViewModel.onEvent(ExportSettingsFile)
            saveAndRestoreSettingsViewModel.onEvent(ExportSettingsFileDialogResult(true))

            coVerify { settingsUtils.exportSettingsFile() }
            assertNotNull(saveAndRestoreSettingsViewModel.viewState.value.snackBarText)
        }

    @Test
    fun `when a user wants to export the settings file a dialog is shown and when the user declines the dialog the export is not started and no snack bar is shown`() =
        runTest {
            coEvery { settingsUtils.exportSettingsFile() } returns true

            saveAndRestoreSettingsViewModel.onEvent(ExportSettingsFile)
            saveAndRestoreSettingsViewModel.onEvent(ExportSettingsFileDialogResult(false))

            coVerify { repeat(0) { settingsUtils.exportSettingsFile() } }
            assertNull(saveAndRestoreSettingsViewModel.viewState.value.snackBarText)
        }

    @Test
    fun `when a user wants to restore the settings from a file a dialog is shown and only when the user accepts the dialog the file import is started when it returns false a snack bar is shown`() =
        runTest {
            coEvery { settingsUtils.restoreSettingsFromFile() } returns false

            saveAndRestoreSettingsViewModel.onEvent(RestoreSettingsFromFile)
            saveAndRestoreSettingsViewModel.onEvent(RestoreSettingsFromFileDialogResult(true))

            coVerify { settingsUtils.restoreSettingsFromFile() }
            assertNotNull(saveAndRestoreSettingsViewModel.viewState.value.snackBarText)
        }

    @Test
    fun `when a user wants to restore the settings from a file a dialog is shown and when the user declines the dialog a snack bar is shown and the import is not started`() =
        runTest {
            coEvery { settingsUtils.restoreSettingsFromFile() } returns true

            saveAndRestoreSettingsViewModel.onEvent(RestoreSettingsFromFile)
            saveAndRestoreSettingsViewModel.onEvent(RestoreSettingsFromFileDialogResult(false))

            coVerify { repeat(0) { settingsUtils.restoreSettingsFromFile() } }
            assertNull(saveAndRestoreSettingsViewModel.viewState.value.snackBarText)
        }

}