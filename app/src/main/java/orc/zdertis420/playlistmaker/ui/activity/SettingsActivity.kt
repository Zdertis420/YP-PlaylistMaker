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
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var views: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModel()

    private val themeInteractor = Creator.provideThemeInteractor(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        views = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(views.root)
        ViewCompat.setOnApplyWindowInsetsListener(views.settings) { view, windowInsetsCompat ->
            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

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