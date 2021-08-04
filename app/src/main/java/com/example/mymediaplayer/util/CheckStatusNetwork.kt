package com.example.mymediaplayer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest

object CheckStatusNetwork {
    var isActive: Boolean = false
        private set

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    fun registerNetworkCallback(context: Context) {
        connectivityManager = context.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object: ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isActive = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isActive = false
            }
        }

        connectivityManager
            .registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
    }
}