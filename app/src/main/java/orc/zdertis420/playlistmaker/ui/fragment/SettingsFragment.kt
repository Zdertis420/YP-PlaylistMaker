package orc.zdertis420.playlistmaker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import orc.zdertis420.playlistmaker.domain.interactor.ThemeInteractor
import orc.zdertis420.playlistmaker.ui.compose.SettingsScreen
import orc.zdertis420.playlistmaker.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private val themeInteractor: ThemeInteractor by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                var isDarkThemeState by remember { mutableStateOf(themeInteractor.getTheme()) }

                SettingsScreen(
                    isDarkTheme = isDarkThemeState,
                    onThemeToggle = { newThemeState ->
                        viewModel.toggleTheme()
                        isDarkThemeState = newThemeState
                    },
                    onShareClick = { viewModel.shareApp() },
                    onSupportClick = { viewModel.contactSupport() },
                    onEulaClick = { viewModel.seeEula() }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.actionLiveData.observe(viewLifecycleOwner) { intent ->
            if (intent != null) {
                startActivity(intent)
                viewModel.resetAction()
            }
        }
    }
}
