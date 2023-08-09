package com.fairmatic.sampleapp.manager

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sampleapp.MyZendriveBroadcastReceiver
import com.fairmatic.sampleapp.MyZendriveNotificationProvider
import com.fairmatic.sampleapp.utils.NotificationUtility
import com.zendrive.sdk.GooglePlaySettingsError
import com.zendrive.sdk.Zendrive
import com.zendrive.sdk.ZendriveConfiguration
import com.zendrive.sdk.ZendriveDriveDetectionMode
import com.zendrive.sdk.ZendriveIssueType
import com.zendrive.sdk.ZendriveOperationCallback
import com.zendrive.sdk.ZendriveOperationResult
import com.zendrive.sdk.ZendriveSettings
import com.zendrive.sdk.ZendriveSettingsCallback
import com.zendrive.sdk.insurance.ZendriveInsurance

class ZendriveManager {
    private inner class InsuranceInfo internal constructor(
        var insurancePeriod: Int,
        var trackingId: String?
    )

    fun initializeZendriveSDK(context: Context?, driverId: String?) {
        if (context?.let { Zendrive.isSDKSetup(it) } == true) {
            return
        }
        Log.d(Constants.LOG_TAG_DEBUG, "initializeZendriveSDK called")
        val zendriveConfiguration = ZendriveConfiguration(
            ZENDRIVE_SDK_KEY, driverId, ZendriveDriveDetectionMode.AUTO_ON
        )
        if (context != null) {
            Zendrive.   setup(
                context,
                zendriveConfiguration,
                MyZendriveBroadcastReceiver::class.java,
                MyZendriveNotificationProvider::class.java
            ) { result ->
                if (result.isSuccess) {
                    Log.d(Constants.LOG_TAG_DEBUG, "ZendriveSDK setup success")
                    // Update periods
                    updateZendriveInsurancePeriod(context)
                    // Hide error if visible
                    if (context != null) {
                        NotificationUtility.hideFairmaticSetupFailureNotification(context)
                    }
                    SharedPrefsManager.sharedInstance(context)?.setRetryFairmaticSetup(false)
                } else {
                    Log.d(
                        Constants.LOG_TAG_DEBUG,
                        java.lang.String.format(
                            "ZendriveSDK setup failed %s",
                            result.errorCode.toString()
                        )
                    )
                    // Display error
                    if (context != null) {
                        NotificationUtility.displayFairmaticSetupFailureNotification(context)
                    }
                    SharedPrefsManager.sharedInstance(context)?.setRetryFairmaticSetup(true)
                }
            }
        }
    }

    fun maybeCheckZendriveSettings(context: Context) {
        val prefsManager = SharedPrefsManager.sharedInstance(context)
        if (prefsManager?.isSettingsErrorFound == true || prefsManager?.isSettingsWarningsFound == true) {
            checkZendriveSettings(context)
        }
    }

    fun checkZendriveSettings(context: Context) {
        // clear all previous setting error notifications.
        NotificationUtility.clearAllErrorNotifications(context)
        Zendrive.getZendriveSettings(context, object : ZendriveSettingsCallback {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onComplete(zendriveSettings: ZendriveSettings?) {
                if (zendriveSettings == null) {
                    // The callback returns null if the SDK is not setup.
                    return
                }

                // Handle errors
                for (error in zendriveSettings.errors) {
                    when (error.type) {
                        ZendriveIssueType.POWER_SAVER_MODE_ENABLED -> {
                            val notification: Notification =
                                NotificationUtility.getPSMNotification(context, true)
                            getNotificationManager(context).notify(
                                NotificationUtility.PSM_ENABLED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        ZendriveIssueType.BACKGROUND_RESTRICTION_ENABLED -> {
                            val notification: Notification =
                                NotificationUtility.getBackgroundRestrictedNotification(context)
                            getNotificationManager(context).notify(
                                NotificationUtility.BACKGROUND_RESTRICTED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        ZendriveIssueType.GOOGLE_PLAY_SETTINGS_ERROR -> {
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

                        ZendriveIssueType.LOCATION_PERMISSION_DENIED -> {
                            val notification: Notification =
                                NotificationUtility.getLocationPermissionDeniedNotification(context)
                            getNotificationManager(context).notify(
                                NotificationUtility.LOCATION_PERMISSION_DENIED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        ZendriveIssueType.LOCATION_SETTINGS_ERROR -> {
                            val notification: Notification =
                                NotificationUtility.getLocationDisabledNotification(context)
                            getNotificationManager(context).notify(
                                NotificationUtility.LOCATION_DISABLED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        ZendriveIssueType.WIFI_SCANNING_DISABLED -> {
                            val notification: Notification =
                                NotificationUtility.getWifiScanningDisabledNotification(context)
                            getNotificationManager(context).notify(
                                NotificationUtility.WIFI_SCANNING_DISABLED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        else -> {}
                    }
                }

                // Handle warnings
                for (warning in zendriveSettings.warnings) {
                    when (warning.type) {
                        ZendriveIssueType.POWER_SAVER_MODE_ENABLED -> {
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

    fun updateZendriveInsurancePeriod(context: Context?) {
        val insuranceCalllback: ZendriveOperationCallback = object : ZendriveOperationCallback {
            override fun onCompletion(zendriveOperationResult: ZendriveOperationResult) {
                if (!zendriveOperationResult.isSuccess()) {
                    Log.d(
                        Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                zendriveOperationResult.errorCode.name
                    )
                }
            }
        }
        val insuranceInfo = currentlyActiveInsurancePeriod(context)
        if (insuranceInfo == null) {
            Log.d(Constants.LOG_TAG_DEBUG, "updateZendriveInsurancePeriod with NO period")
            if (context != null) {
                ZendriveInsurance.stopPeriod(context, insuranceCalllback)
            }
        } else if (insuranceInfo.insurancePeriod == 3) {
            Log.d(
                Constants.LOG_TAG_DEBUG, String.format(
                    "updateZendriveInsurancePeriod with period %d and id: %s",
                    insuranceInfo.insurancePeriod,
                    insuranceInfo.trackingId
                )
            )
            if (context != null) {
                insuranceInfo.trackingId?.let {
                    ZendriveInsurance.startDriveWithPeriod3(
                        context, it,
                        insuranceCalllback
                    )
                }
            }
        } else if (insuranceInfo.insurancePeriod == 2) {
            Log.d(
                Constants.LOG_TAG_DEBUG, String.format(
                    "updateZendriveInsurancePeriod with period %d and id: %s",
                    insuranceInfo.insurancePeriod,
                    insuranceInfo.trackingId
                )
            )
            if (context != null) {
                insuranceInfo.trackingId?.let {
                    ZendriveInsurance.startDriveWithPeriod2(
                        context, it,
                        insuranceCalllback
                    )
                }
            }
        } else {
            Log.d(
                Constants.LOG_TAG_DEBUG, String.format(
                    "updateZendriveInsurancePeriod with period %d",
                    insuranceInfo.insurancePeriod
                )
            )
            if (context != null) {
                ZendriveInsurance.startPeriod1(context, insuranceCalllback)
            }
        }
    }

    private fun currentlyActiveInsurancePeriod(context: Context?): InsuranceInfo? {
        val state = TripManager.sharedInstance(
            context!!
        )!!.tripManagerState
        return if (!state.isUserOnDuty) {
            null
        } else if (state.passengerInCar ) {
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
        private const val ZENDRIVE_SDK_KEY = "" // Your Zendrive SDK Key
        private val sharedInstance = ZendriveManager()
        @Synchronized
        fun sharedInstance(): ZendriveManager {
            return sharedInstance
        }
    }
}