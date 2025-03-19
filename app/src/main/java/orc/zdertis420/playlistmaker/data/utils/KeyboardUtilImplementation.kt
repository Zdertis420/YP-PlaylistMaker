package orc.zdertis420.playlistmaker.data.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import orc.zdertis420.playlistmaker.domain.utils.KeyboardUtil

class KeyboardUtilImplementation(private val context: Context) : KeyboardUtil {
    override fun hideKeyboard(view: View) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }
}