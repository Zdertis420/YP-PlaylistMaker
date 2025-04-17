package orc.zdertis420.playlistmaker.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.FragmentSettingsBinding
import orc.zdertis420.playlistmaker.domain.interactor.ThemeInteractor
import orc.zdertis420.playlistmaker.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment(), View.OnClickListener {

    private var _views: FragmentSettingsBinding? = null
    private val views get() = _views!!

    private val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()

    private val themeInteractor by inject<ThemeInteractor>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _views = FragmentSettingsBinding.inflate(inflater, container, false)

        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            viewModel.actionLiveData.observe(viewLifecycleOwner) { intent ->
                startActivity(intent)
            }

            views.switchTheme.isChecked = themeInteractor.getTheme()

            views.switchTheme.setOnCheckedChangeListener { switch, state ->
                Log.i("THEME", state.toString())
                viewModel.toggleTheme()
                Log.d("THEME", state.toString())
            }

            views.share.setOnClickListener(this)
            views.support.setOnClickListener(this)
            views.eula.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.share -> viewModel.shareApp()

            R.id.support -> viewModel.contactSupport()

            R.id.eula -> viewModel.seeEula()
        }
    }
}