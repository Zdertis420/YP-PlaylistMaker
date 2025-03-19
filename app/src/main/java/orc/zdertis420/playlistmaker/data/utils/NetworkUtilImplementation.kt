package orc.zdertis420.playlistmaker.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import orc.zdertis420.playlistmaker.domain.utils.NetworkUtil

class NetworkUtilImplementation(private val context: Context) : NetworkUtil {
    override fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}