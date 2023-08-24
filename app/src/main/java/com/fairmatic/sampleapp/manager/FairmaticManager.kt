package com.fairmatic.sampleapp.manager

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sampleapp.MyFairmaticBroadcastReceiver
import com.fairmatic.sampleapp.MyFairmaticNotificationProvider
import com.fairmatic.sampleapp.R
import com.fairmatic.sampleapp.fragments.OnDutyFragment
import com.fairmatic.sampleapp.utils.NotificationUtility
import com.fairmatic.sdk.Fairmatic
import com.fairmatic.sdk.classes.FairmaticConfiguration
import com.fairmatic.sdk.classes.FairmaticDriveDetectionMode
import com.fairmatic.sdk.classes.FairmaticDriverAttributes
import com.fairmatic.sdk.classes.FairmaticIssueType
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult
import com.fairmatic.sdk.classes.FairmaticSettings
import com.fairmatic.sdk.classes.FairmaticSettingsCallback
import com.fairmatic.sdk.classes.GooglePlaySettingsError

class FairmaticManager {
    inner class InsuranceInfo internal constructor(
        var insurancePeriod: Int,
        var trackingId: String?
    )

    private val fairmaticDriverAttributes = FairmaticDriverAttributes(
        name = "John Doe",
        email = "john_doe@company.com",
        phoneNumber = "1234567890"
    )
    fun initializeFairmaticSDK(
        context: Context?,
        driverId: String?,
        callback: FairmaticOperationCallback
    ) {
        Log.d(Constants.LOG_TAG_DEBUG, "initializeFairmaticSDK called")
        val fairmaticConfiguration = driverId?.let {
            FairmaticConfiguration(
                Fairmatic_SDK_KEY, it, fairmaticDriverAttributes
            )
        }?.apply {
            setDriveDetectionMode(FairmaticDriveDetectionMode.AUTO_ON)
        }
        if (context != null) {
            if (fairmaticConfiguration != null) {
                Fairmatic.setup(
                    context,
                    fairmaticConfiguration,
                    MyFairmaticBroadcastReceiver::class.java,
                    MyFairmaticNotificationProvider::class.java,
                    object : FairmaticOperationCallback {
                        override fun onCompletion(result: FairmaticOperationResult) {
                            if (result is FairmaticOperationResult.Success) {
                                Log.d(Constants.LOG_TAG_DEBUG, "FairmaticSDK setup success")
                                // Update periods
                                updateFairmaticInsurancePeriod(context)
                                // Hide error if visible
                                if (context != null) {
                                    NotificationUtility.hideFairmaticSetupFailureNotification(context)
                                }
                                SharedPrefsManager.sharedInstance(context)?.setRetryFairmaticSetup(false)
                            } else if(result is FairmaticOperationResult.Failure){
                                Log.d(
                                    Constants.LOG_TAG_DEBUG,
                                    java.lang.String.format(
                                        "FairmaticSDK setup failed %s",
                                        result.error.name
                                    )
                                )
                                // Display error
                                NotificationUtility.displayFairmaticSetupFailureNotification(context)
                                SharedPrefsManager.sharedInstance(context)?.setRetryFairmaticSetup(true)
                            }
                            callback.onCompletion(result)
                        }
                    }
                )
            }
        }
    }

    fun maybeCheckFairmaticSettings(context: Context) {
        val prefsManager = SharedPrefsManager.sharedInstance(context)
        if (prefsManager?.isSettingsErrorFound == true || prefsManager?.isSettingsWarningsFound == true) {
            checkFairmaticSettings(context)
        }
    }

    fun checkFairmaticSettings(context: Context) {
        // clear all previous setting error notifications.
        NotificationUtility.clearAllErrorNotifications(context)
        Fairmatic.getFairmaticSettings(context, object : FairmaticSettingsCallback {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onComplete(fairmaticSettings: FairmaticSettings?) {
                if (fairmaticSettings == null) {
                    // The callback returns null if the SDK is not setup.
                    return
                }

                // Handle errors
                for (error in fairmaticSettings.errors) {
                    when (error.type) {
                        FairmaticIssueType.POWER_SAVER_MODE_ENABLED -> {
                            val notification: Notification =
                                NotificationUtility.getPSMNotification(context, true)
                            getNotificationManager(context).notify(
                                NotificationUtility.PSM_ENABLED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        FairmaticIssueType.BACKGROUND_RESTRICTION_ENABLED -> {
                            val notification: Notification =
                                NotificationUtility.getBackgroundRestrictedNotification(context)
                            getNotificationManager(context).notify(
                                NotificationUtility.BACKGROUND_RESTRICTED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        FairmaticIssueType.GOOGLE_PLAY_SETTINGS_ERROR -> {
                            val e: GooglePlaySettingsError = error as GooglePlaySettingsError
                            val notification: Notification =
                                NotificationUtility.getGooglePlaySettingsNotification(
                                    context,
                                    e.googlePlaySettingsResult
                                )
                            getNotificationManager(context).notify(
                                NotificationUtility.GOOGLE_PLAY_SETTINGS_NOTIFICATION_ID,
                                notification
                            )
                        }

                        FairmaticIssueType.LOCATION_PERMISSION_DENIED -> {
                            val notification: Notification =
                                NotificationUtility.getLocationPermissionDeniedNotification(context)
                            getNotificationManager(context).notify(
                                NotificationUtility.LOCATION_PERMISSION_DENIED_NOTIFICATION_ID,
                                notification
                            )
                        }
                        else -> {}
                    }
                }

                // Handle warnings
                for (warning in fairmaticSettings.warnings) {
                    when (warning.type) {
                        FairmaticIssueType.POWER_SAVER_MODE_ENABLED -> {
                            val notification: Notification =
                                NotificationUtility.getPSMNotification(context, false)
                            getNotificationManager(context).notify(
                                NotificationUtility.PSM_ENABLED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        else -> {}
                    }
                }
            }
        })
    }


    fun handleInsurancePeriod1(context : Context?, callback : FairmaticOperationCallback?){
        if (context != null) {
            Fairmatic.startDriveWithPeriod1(context, callback)
        }
    }

    fun handleInsurancePeriod2(context: Context?, callback: FairmaticOperationCallback?) {
        if (context != null) {
            System.currentTimeMillis().toString().let {
                Fairmatic.startDriveWithPeriod2(context, it, callback)
            }

        }
    }

    fun handleInsurancePeriod3(context: Context?, callback: FairmaticOperationCallback?){
        if (context != null) {
            System.currentTimeMillis().toString().let {
                Fairmatic.startDriveWithPeriod3(context, it, callback)
            }
        }
    }

    fun handleStopPeriod(context: Context?, callback: FairmaticOperationCallback?){
        if (context != null) {
            Fairmatic.stopPeriod(context, callback)
        }
    }




    fun updateFairmaticInsurancePeriod(context: Context?) {
        /*val insuranceCallback: FairmaticOperationCallback = object : FairmaticOperationCallback {
            override fun onCompletion(fairmaticOperationResult: FairmaticOperationResult) {
                if (fairmaticOperationResult is FairmaticOperationResult.Success) {
                    Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switch success")

                }
                if (fairmaticOperationResult is FairmaticOperationResult.Failure) {
                    Log.d(
                        Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                fairmaticOperationResult.error.name
                    )
                }
            }
        }*/
        val insuranceInfo = currentlyActiveInsurancePeriod(context)
        Log.d("check123", "updateFairmaticInsurancePeriod: ${insuranceInfo?.insurancePeriod}")
        if (insuranceInfo == null) {
            Log.d(Constants.LOG_TAG_DEBUG, "updateFairmaticInsurancePeriod with NO period")
            if (context != null) {
                handleStopPeriod(context, object : FairmaticOperationCallback {
                    override fun onCompletion(result: FairmaticOperationResult) {
                        //TODO("Not yet implemented")
                    }
                })
            }
        } else if (insuranceInfo.insurancePeriod == 3) {
            handleInsurancePeriod3(context, object : FairmaticOperationCallback{
                override fun onCompletion(result: FairmaticOperationResult) {
                    //TODO("Not yet implemented")
                }
            })
        } else if (insuranceInfo.insurancePeriod == 2) {
            handleInsurancePeriod2(context, object : FairmaticOperationCallback{
                override fun onCompletion(result: FairmaticOperationResult) {
                    //TODO("Not yet implemented")
                }
            })
        } else {
            handleInsurancePeriod1(context, object : FairmaticOperationCallback {
                override fun onCompletion(result: FairmaticOperationResult) {
                    //TODO("Not yet implemented")
                }
            })
        }
    }

    fun currentlyActiveInsurancePeriod(context: Context?): InsuranceInfo? {
        val state = TripManager.sharedInstance(
            context!!
        )!!.tripManagerState
        return if (!state.isUserOnDuty) {
            null
        } else if (state.passengerInCar) {
            InsuranceInfo(3, state.trackingId)
        } else if (state.passengerWaitingForPickup) {
            InsuranceInfo(2, state.trackingId)
        } else {
            InsuranceInfo(1, null)
        }
    }

    private fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        // TODO - remove this before submit.
        private const val Fairmatic_SDK_KEY = "UXBDuLRFg6k2YT3oys2T9njD8BEzAoA1" // Your Fairmatic SDK Key
        private val sharedInstance = FairmaticManager()
        @Synchronized
        fun sharedInstance(): FairmaticManager {
            return sharedInstance
        }
    }
}