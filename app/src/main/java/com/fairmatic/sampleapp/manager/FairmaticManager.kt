package com.fairmatic.sampleapp.manager

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sampleapp.MyFairmaticBroadcastReceiver
import com.fairmatic.sampleapp.MyFairmaticNotificationProvider
import com.fairmatic.sampleapp.utils.NotificationUtility
import com.fairmatic.sampleapp.utils.SettingIntent
import com.fairmatic.sdk.Fairmatic
import com.fairmatic.sdk.classes.FairmaticConfiguration
import com.fairmatic.sdk.classes.FairmaticDriverAttributes
import com.fairmatic.sdk.classes.FairmaticIssueType
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult
import com.fairmatic.sdk.classes.FairmaticSettings
import com.fairmatic.sdk.classes.FairmaticSettingsCallback
import com.fairmatic.sdk.classes.GooglePlaySettingsError

object FairmaticManager {

    // TODO - Add your Fairmatic SDK Key
    private const val Fairmatic_SDK_KEY =  ""

    private val fairmaticDriverAttributes = FairmaticDriverAttributes(
        name = "John Doe",
        email = "john_doe@company.com",
        phoneNumber = "1234567890"
    )

    fun initializeFairmaticSDK(
        context: Context,
        driverId: String,
        callback: FairmaticOperationCallback
    ) {
        Log.d(Constants.LOG_TAG_DEBUG, "initializeFairmaticSDK called")
        val fairmaticConfiguration = FairmaticConfiguration(
            Fairmatic_SDK_KEY, driverId, fairmaticDriverAttributes
        )

        // Fairmatic SDK setup
        Fairmatic.setup(
            context,
            fairmaticConfiguration,
            MyFairmaticBroadcastReceiver::class.java,
            MyFairmaticNotificationProvider::class.java,
            object : FairmaticOperationCallback {
                override fun onCompletion(result: FairmaticOperationResult) {
                    if (result is FairmaticOperationResult.Success) {
                        Log.d(Constants.LOG_TAG_DEBUG, "FairmaticSDK setup success")
                        // Hide error if visible
                        NotificationUtility.hideFairmaticSetupFailureNotification(context)
                    } else if (result is FairmaticOperationResult.Failure) {
                        Log.d(
                            Constants.LOG_TAG_DEBUG,
                            java.lang.String.format(
                                "FairmaticSDK setup failed %s",
                                result.error.name
                            )
                        )
                        // Display error
                        NotificationUtility.displayFairmaticSetupFailureNotification(context)
                    }
                    callback.onCompletion(result)
                }
            }
        )
    }

    // Check for Fairmatic settings errors and warnings
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
                    Log.d("fmsdk", "error: ${error.type}")
                    val actionIntent = SettingIntent.forError(error.type, context) ?: continue
                    when (error.type) {
                        FairmaticIssueType.POWER_SAVER_MODE_ENABLED -> {
                            val notification: Notification =
                                NotificationUtility.getPSMNotification(context, actionIntent, true)
                            getNotificationManager(context).notify(
                                NotificationUtility.PSM_ENABLED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        FairmaticIssueType.BACKGROUND_RESTRICTION_ENABLED -> {
                            val notification: Notification =
                                NotificationUtility.getBackgroundRestrictedNotification(context, actionIntent)
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
                                NotificationUtility.getLocationPermissionDeniedNotification(context, actionIntent)
                            getNotificationManager(context).notify(
                                NotificationUtility.LOCATION_PERMISSION_DENIED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        FairmaticIssueType.ACTIVITY_RECOGNITION_PERMISSION_DENIED -> {
                            val notification: Notification =
                                NotificationUtility.getActivityRecognitionPermissionDeniedNotification(context, actionIntent)
                            getNotificationManager(context).notify(
                                NotificationUtility.ACTIVITY_RECOGNITION_DENIED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        else -> {}
                    }
                }

                // Handle warnings
                for (warning in fairmaticSettings.warnings) {
                    val actionIntent = SettingIntent.forError(warning.type, context) ?: continue
                    when (warning.type) {
                        FairmaticIssueType.POWER_SAVER_MODE_ENABLED -> {
                            val notification: Notification =
                                NotificationUtility.getPSMNotification(context, actionIntent, false)
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

    // Start insurance period 1
    fun startInsurancePeriod1(context: Context, callback: FairmaticOperationCallback?) {
        Log.d(Constants.LOG_TAG_DEBUG, "Start insurance period 1 called")
        Fairmatic.startDriveWithPeriod1(context, callback)
    }

    // Start insurance period 2
    fun startInsurancePeriod2(context: Context, callback: FairmaticOperationCallback?) {
        Log.d(Constants.LOG_TAG_DEBUG, "Start insurance period 2 called")
        Fairmatic.startDriveWithPeriod2(
            context,
            System.currentTimeMillis().toString(),
            callback)
    }

    // Start insurance period 3
    fun startInsurancePeriod3(context: Context, callback: FairmaticOperationCallback?) {
        Log.d(Constants.LOG_TAG_DEBUG, "Start insurance period 3 called")
        Fairmatic.startDriveWithPeriod3(
            context,
            System.currentTimeMillis().toString(),
            callback)
    }

    // Stop insurance period
    fun stopPeriod(context: Context, callback: FairmaticOperationCallback?) {
        Log.d(Constants.LOG_TAG_DEBUG, "Stop period called")
        Fairmatic.stopPeriod(context, callback)
    }

    private fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}