package orc.zdertis420.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var backToMain: MaterialToolbar
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

        bind()
    }

    private fun bind() {
        backToMain = findViewById(R.id.back_to_main)
        share = findViewById(R.id.share)
        support = findViewById(R.id.support)
        eula = findViewById(R.id.eula)

        backToMain.setOnClickListener(this@SettingsActivity)
        share.setOnClickListener(this@SettingsActivity)
        support.setOnClickListener(this@SettingsActivity)
        eula.setOnClickListener(this@SettingsActivity)
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