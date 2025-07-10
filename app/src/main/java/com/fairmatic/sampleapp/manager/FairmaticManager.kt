package com.fairmatic.sampleapp.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sampleapp.R
import com.fairmatic.sampleapp.utils.NotificationUtility
import com.fairmatic.sampleapp.utils.SettingIntent
import com.fairmatic.sdk.Fairmatic
import com.fairmatic.sdk.classes.FairmaticConfiguration
import com.fairmatic.sdk.classes.FairmaticDriverAttributes
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult
import com.fairmatic.sdk.classes.FairmaticSettingError
import com.fairmatic.sdk.classes.FairmaticSettingsCallback
import com.fairmatic.sdk.classes.FairmaticTripNotification
import com.fairmatic.sdk.classes.TripNotificationContainer
import com.fairmatic.sdk.classes.TripNotificationContainerProvider

object FairmaticManager {

    // TODO - Add your Fairmatic SDK Key
    private const val Fairmatic_SDK_KEY =  ""

    private val fairmaticDriverAttributes = FairmaticDriverAttributes(
        firstName = "John",
        lastName = "Doe",
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
            FairmaticTripNotification(
                "Fairmatic",
                "Fairmatic",
                R.drawable.notification_icon,
            ),
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

        /**
         * Optionally configure dynamic trip notifications using a lambda.
         * This allows customizing the notification content at runtime based on your business logic.
         * It ensures the notification reflects the most up-to-date information every time it's shown.
         */
        Fairmatic.configureTripNotification(object: TripNotificationContainerProvider{
            override fun get(): TripNotificationContainer {
                // Create notification channel
                val channelId = "example_channel"
                val channelName = "Example Channel Name"
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Example channel for Fairmatic trip notifications"
                }
                notificationManager.createNotificationChannel(channel)

                // Create notification
                val timestamp = System.currentTimeMillis()
                val notification = NotificationCompat.Builder(context, channelId)
                    .setContentTitle("Example notification")
                    .setContentText("Timestamp: $timestamp")
                    .setSmallIcon(R.drawable.notification_icon)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

                // Set notification ID
                val notificationId = Int.MIN_VALUE

                return TripNotificationContainer(channel, notification, notificationId)
            }
        })
    }

    // Check for Fairmatic settings errors and warnings
    fun checkFairmaticSettings(context: Context) {
        // clear all previous setting error notifications.
        NotificationUtility.clearAllErrorNotifications(context)
        Fairmatic.getFairmaticSettings(context, object : FairmaticSettingsCallback {
            override fun onComplete(errors: List<FairmaticSettingError>) {
                if (errors.isEmpty()) {
                    // The callback returns null if the SDK is not setup.
                    return
                }

                // Handle errors
                for (error in errors) {
                    Log.d("fmsdk", "error: $error")
                    val actionIntent = SettingIntent.forError(error, context) ?: continue
                    when (error) {
                        FairmaticSettingError.BACKGROUND_RESTRICTION_ENABLED -> {
                            val notification: Notification =
                                NotificationUtility.getBackgroundRestrictedNotification(context, actionIntent)
                            getNotificationManager(context).notify(
                                NotificationUtility.BACKGROUND_RESTRICTED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        FairmaticSettingError.LOCATION_PERMISSION_DENIED -> {
                            val notification: Notification =
                                NotificationUtility.getLocationPermissionDeniedNotification(context, actionIntent)
                            getNotificationManager(context).notify(
                                NotificationUtility.LOCATION_PERMISSION_DENIED_NOTIFICATION_ID,
                                notification
                            )
                        }

                        FairmaticSettingError.ACTIVITY_RECOGNITION_PERMISSION_DENIED -> {
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
            }
        })
    }

    // Start insurance period 1
    fun startInsurancePeriod1(context: Context, callback: FairmaticOperationCallback?) {
        Log.d(Constants.LOG_TAG_DEBUG, "Start insurance period 1 called")
        Fairmatic.startDriveWithPeriod1(context, System.currentTimeMillis().toString(), callback)
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