package orc.zdertis420.playlistmaker

import android.content.Intent
import android.net.Uri
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

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var backToMain: MaterialToolbar
    private lateinit var switchTheme: SwitchMaterial
    private lateinit var share: TextView
    private lateinit var support: TextView
    private lateinit var eula: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val preferences = getSharedPreferences("THEME_SETTING", MODE_PRIVATE)

        backToMain = findViewById(R.id.back_to_main)
        switchTheme = findViewById(R.id.switch_theme)
        share = findViewById(R.id.share)
        support = findViewById(R.id.support)
        eula = findViewById(R.id.eula)

        switchTheme.isChecked = preferences.getBoolean("THEME", false)

        backToMain.setOnClickListener(this)

        switchTheme.setOnCheckedChangeListener { switch, state ->
            Log.i("THEME", state.toString())
            (applicationContext as App).switchTheme(state)
            preferences.edit()
                .putBoolean("THEME", state)
                .apply()
        }

        share.setOnClickListener(this)
        support.setOnClickListener(this)
        eula.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_to_main -> finish()

            R.id.share -> startActivity(Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.course)
                )
                type = "text/plain"
            }, null))

            R.id.support -> startActivity(Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")

                putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(getString(R.string.email))
                )
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.subject)
                )
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.text)
                )
            }, null))

            R.id.eula -> startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.offer)))
            )
        }
    }
}