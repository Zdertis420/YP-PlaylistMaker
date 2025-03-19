package orc.zdertis420.playlistmaker.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import orc.zdertis420.playlistmaker.App
import orc.zdertis420.playlistmaker.Creator
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var views: ActivitySettingsBinding

    private val shareAppUseCase = Creator.provideShareAppUseCase(this)
    private val contactSupportUseCase = Creator.provideContactSupportUSeCase(this)
    private val seeEulaUseCase = Creator.provideSeeEulaUseCase(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        views = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(views.root)

        val themeInteractor = Creator.provideThemeInteractor(applicationContext)

        views.switchTheme.isChecked = themeInteractor.getTheme()

        views.backToMain.setOnClickListener(this)

        views.switchTheme.setOnCheckedChangeListener { switch, state ->
            Log.i("THEME", state.toString())
            (applicationContext as App).switchTheme(state)
            themeInteractor.saveTheme(state)
        }

        views.share.setOnClickListener(this)
        views.support.setOnClickListener(this)
        views.eula.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_to_main -> finish()

            R.id.share -> shareAppUseCase.shareApp()

            R.id.support -> contactSupportUseCase.contactSupport()

            R.id.eula -> seeEulaUseCase.seeEula()
        }
    }
}