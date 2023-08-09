package com.fairmatic.sampleapp

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.fairmatic.sampleapp.manager.FairmaticManager
import com.fairmatic.sampleapp.manager.SharedPrefsManager
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult

class SampleAppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val driverId: String? = SharedPrefsManager.sharedInstance(this).driverId
        if (!driverId.isNullOrBlank()) {
            FairmaticManager.initializeFairmaticSDK(this, driverId, object :
                FairmaticOperationCallback {
                override fun onCompletion(result: FairmaticOperationResult) {
                    if (result is FairmaticOperationResult.Failure) {
                        val errorMessage = result.error
                        Toast.makeText(
                            this@SampleAppApplication,
                            "Failed to initialize Fairmatic SDK : ${errorMessage.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@SampleAppApplication,
                            "Successfully initialized Fairmatic SDK",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }
}