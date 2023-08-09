package com.fairmatic.sampleapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fairmatic.sampleapp.Constants

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // This method is a no-op as Fairmatic SDK initialisation has already happened in the Application class.
        Log.d (Constants.LOG_TAG_DEBUG, "BootReceiver onReceive called with intent ${intent?.action}" )
    }
}