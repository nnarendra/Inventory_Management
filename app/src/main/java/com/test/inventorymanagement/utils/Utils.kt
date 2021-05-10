package com.test.inventorymanagement.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot


object Utils {

    fun getString(doc: DocumentSnapshot, query: String): String {
        return doc.data?.get(query).toString()
    }

    fun getInt(doc: DocumentSnapshot, query: String): Int {
        return doc.data?.get(query) as? Int ?: 0
    }

    fun isConnectedToNetwork(context: Context): Boolean {
        var connected = false
        try {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo = cm.activeNetworkInfo
            connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
            return connected
        } catch (e: Exception) {
            Log.e("Connectivity Exception", e.message!!)
        }
        return connected
    }

}