package orc.zdertis420.playlistmaker.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import orc.zdertis420.playlistmaker.Creator
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.ActivitySettingsBinding
import orc.zdertis420.playlistmaker.ui.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var views: ActivitySettingsBinding

    private lateinit var viewModel: SettingsViewModel

    private val shareAppUseCase = Creator.provideShareAppUseCase(this)
    private val contactSupportUseCase = Creator.provideContactSupportUSeCase(this)
    private val seeEulaUseCase = Creator.provideSeeEulaUseCase(this)
    private val themeInteractor = Creator.provideThemeInteractor(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.v("ACTIVITY", "CREATED")

        views = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(views.root)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(themeInteractor, shareAppUseCase, contactSupportUseCase, seeEulaUseCase) as T
            }
        })[SettingsViewModel::class.java]

        views.switchTheme.isChecked = themeInteractor.getTheme()

        views.backToMain.setOnClickListener(this)

        views.switchTheme.setOnCheckedChangeListener { switch, state ->
            Log.i("THEME", state.toString())
            viewModel.toggleTheme()
        }

        views.share.setOnClickListener(this)
        views.support.setOnClickListener(this)
        views.eula.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_to_main -> finish()

            R.id.share -> viewModel.shareApp()

            R.id.support -> viewModel.contactSupport()

            R.id.eula -> viewModel.seeEula()
        }
    }
}