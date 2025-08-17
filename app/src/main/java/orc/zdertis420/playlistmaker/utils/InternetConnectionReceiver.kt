package orc.zdertis420.playlistmaker.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

class InternetConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.net.conn.CONNECTIVITY_CHANGE") {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val isConnected = cm?.activeNetworkInfo?.isConnectedOrConnecting == true

            if (!isConnected) Toast.makeText(context, "Соединение потеряно", Toast.LENGTH_SHORT).show()
        }
    }
}