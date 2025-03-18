package orc.zdertis420.playlistmaker.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import orc.zdertis420.playlistmaker.App
import orc.zdertis420.playlistmaker.Creator
import orc.zdertis420.playlistmaker.R

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var backToMain: MaterialToolbar
    private lateinit var switchTheme: SwitchMaterial
    private lateinit var share: TextView
    private lateinit var support: TextView
    private lateinit var eula: TextView

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

        val themeInteractor = Creator.provideThemeInteractor(applicationContext)


        backToMain = findViewById(R.id.back_to_main)
        switchTheme = findViewById(R.id.switch_theme)
        share = findViewById(R.id.share)
        support = findViewById(R.id.support)
        eula = findViewById(R.id.eula)

        switchTheme.isChecked = themeInteractor.getTheme()

        backToMain.setOnClickListener(this)

        switchTheme.setOnCheckedChangeListener { switch, state ->
            Log.i("THEME", state.toString())
            (applicationContext as App).switchTheme(state)
            themeInteractor.saveTheme(state)
        }

        share.setOnClickListener(this)
        support.setOnClickListener(this)
        eula.setOnClickListener(this)
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