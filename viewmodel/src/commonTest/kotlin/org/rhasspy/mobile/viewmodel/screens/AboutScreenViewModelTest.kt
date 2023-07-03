package org.rhasspy.mobile.viewmodel.screens

import org.kodein.mock.Fake
import org.kodein.mock.Mock
import org.koin.dsl.module
import org.koin.test.get
import org.rhasspy.mobile.data.libraries.StableLibrary
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.platformspecific.utils.IOpenLinkUtils
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.nVerify
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action.OpenSourceCode
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewModel
import kotlin.test.*

class AboutScreenViewModelTest : AppTest() {

    @Mock
    lateinit var openLinkUtils: IOpenLinkUtils

    @Fake
    lateinit var library: StableLibrary

    @Fake
    lateinit var library2: StableLibrary

    private lateinit var aboutScreenViewModel: AboutScreenViewModel
    private lateinit var navigator: Navigator

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { openLinkUtils }
            }
        )

        aboutScreenViewModel = get()
        aboutScreenViewModel.onComposed()
        navigator = get()
    }

    @Test
    fun `when user wants to open source code he is redirected to github`() {
        every { openLinkUtils.openLink(isAny()) } returns true

        aboutScreenViewModel.onEvent(OpenSourceCode)

        nVerify { openLinkUtils.openLink(LinkType.SourceCode) }
    }

    @Test
    fun `when user wants to open source code and can't be redirected then snackBar is shown and consumed`() {
        every { openLinkUtils.openLink(isAny()) } returns false

        aboutScreenViewModel.onEvent(OpenSourceCode)

        nVerify { openLinkUtils.openLink(LinkType.SourceCode) }
        assertNotEquals(null, aboutScreenViewModel.viewState.value.snackBarText)

        aboutScreenViewModel.onEvent(ShowSnackBar)
        assertEquals(null, aboutScreenViewModel.viewState.value.snackBarText)
    }

    @Test
    fun `when user wants to open changelog dialog it's shown and user can close it`() {

        aboutScreenViewModel.onEvent(OpenChangelog)
        assertTrue { aboutScreenViewModel.viewState.value.isChangelogDialogVisible }

        aboutScreenViewModel.onEvent(CloseChangelog)
        assertFalse { aboutScreenViewModel.viewState.value.isChangelogDialogVisible }

        aboutScreenViewModel.onEvent(OpenChangelog)
        assertTrue { aboutScreenViewModel.viewState.value.isChangelogDialogVisible }

        navigator.onBackPressed()
        assertFalse { aboutScreenViewModel.viewState.value.isChangelogDialogVisible }
    }

    @Test
    fun `when user wants to open privacy dialog it's shown and user can close it`() {

        aboutScreenViewModel.onEvent(OpenDataPrivacy)
        assertTrue { aboutScreenViewModel.viewState.value.isPrivacyDialogVisible }

        aboutScreenViewModel.onEvent(CloseDataPrivacy)
        assertFalse { aboutScreenViewModel.viewState.value.isPrivacyDialogVisible }

        aboutScreenViewModel.onEvent(OpenDataPrivacy)
        assertTrue { aboutScreenViewModel.viewState.value.isPrivacyDialogVisible }

        navigator.onBackPressed()
        assertFalse { aboutScreenViewModel.viewState.value.isPrivacyDialogVisible }
    }

    @Test
    fun `when user wants to open any library dialog it's shown and user can close it`() {

        aboutScreenViewModel.onEvent(OpenLibrary(library))
        assertTrue { aboutScreenViewModel.viewState.value.isLibraryDialogVisible }
        assertEquals(library, aboutScreenViewModel.viewState.value.libraryDialogContent)

        aboutScreenViewModel.onEvent(CloseLibrary)
        assertFalse { aboutScreenViewModel.viewState.value.isLibraryDialogVisible }

        aboutScreenViewModel.onEvent(OpenLibrary(library2))
        assertTrue { aboutScreenViewModel.viewState.value.isLibraryDialogVisible }
        assertEquals(library2, aboutScreenViewModel.viewState.value.libraryDialogContent)

        navigator.onBackPressed()
        assertFalse { aboutScreenViewModel.viewState.value.isLibraryDialogVisible }
    }
}