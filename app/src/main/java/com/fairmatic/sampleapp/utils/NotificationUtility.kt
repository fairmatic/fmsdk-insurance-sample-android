package com.fairmatic.sampleapp.utils

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sampleapp.Constants.ACTIVITY_RECOGNITION_REQUEST
import com.fairmatic.sampleapp.MainActivity
import com.fairmatic.sampleapp.R
import com.fairmatic.sampleapp.SettingsCheckActivity
import com.google.android.gms.location.LocationSettingsResult

object NotificationUtility {
    var LOCATION_PERMISSION_DENIED_NOTIFICATION_ID = 100
    var ACTIVITY_RECOGNITION_DENIED_NOTIFICATION_ID = 101
    var PSM_ENABLED_NOTIFICATION_ID = 103
    var BACKGROUND_RESTRICTED_NOTIFICATION_ID = 105
    var WIFI_SCANNING_DISABLED_NOTIFICATION_ID = 107
    var GOOGLE_PLAY_SETTINGS_NOTIFICATION_ID = 108
    private const val Fairmatic_FAILED_NOTIFICATION_ID = 4
    private const val PSM_ENABLED_REQUEST_CODE = 200
    private const val LOCATION_DISABLED_REQUEST_CODE = 201
    private const val BACKGROUND_RESTRICTED_REQUEST_CODE = 202
    private const val WIFI_SCANNING_REQUEST_CODE = 204
    private const val GOOGLE_PLAY_SETTINGS_REQUEST_CODE = 205
    private const val LOCATION_PERMISSION_REQUEST_CODE = 206
    private const val FOREGROUND_CHANNEL_KEY = "Foreground"
    private const val ISSUES_CHANNEL_KEY = "Issues"

    /**
     * Creates a notification to be displayed when the SDK is tracking a trip.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getInDriveNotification(context: Context): Notification {
        createNotificationChannels(context)
        return Notification.Builder(context, FOREGROUND_CHANNEL_KEY)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.app_name))
            .setCategory(androidx.core.app.NotificationCompat.CATEGORY_SERVICE)
            .setContentText("Drive Active.").build()
    }

    /**
     * Creates a notification to be displayed when the SDK is detecting a potential trip.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMaybeInDriveNotification(context: Context): Notification {
        createNotificationChannels(context)
        return Notification.Builder(context, FOREGROUND_CHANNEL_KEY)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.app_name))
            .setCategory(androidx.core.app.NotificationCompat.CATEGORY_SERVICE)
            .setContentText("Detecting Possible Drive.").build()
    }

    /**
     * Creates a notification to be displayed when Power saver mode is enabled
     * on the device.
     */
    @RequiresApi(Build.VERSION_CODES.O)
     // This error shouldn't be sent below this.
    fun getPSMNotification(context: Context, actionIntent: Intent, isError: Boolean): Notification {
        createNotificationChannels(context)
        val pi: PendingIntent = PendingIntent.getActivity(
            context, PSM_ENABLED_REQUEST_CODE,
            actionIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val titleTickerPrefix = if (isError) "Error:" else "Warning:"
        return Notification.Builder(context, ISSUES_CHANNEL_KEY)
            .setContentTitle("$titleTickerPrefix Power Saver Mode Enabled")
            .setTicker("$titleTickerPrefix power Saver Mode Enabled")
            .setContentText("Disable power saver mode.")
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    /**
     * Creates a notification to be displayed when background restrictions are enabled for
     * the application.
     */
    @TargetApi(Build.VERSION_CODES.P)
    fun getBackgroundRestrictedNotification(context: Context, actionIntent: Intent): Notification {
        createNotificationChannels(context)
        actionIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pi: PendingIntent = PendingIntent.getActivity(
            context, BACKGROUND_RESTRICTED_REQUEST_CODE,
            actionIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Builder(context, ISSUES_CHANNEL_KEY)
            .setContentTitle("Background Restricted")
            .setTicker("Background Restricted")
            .setContentText("Disable Background Restriction")
            .setOnlyAlertOnce(true)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    /**
     * Creates a notification to be displayed when location permission is denied to the application.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocationPermissionDeniedNotification(context: Context, actionIntent: Intent): Notification {
        createNotificationChannels(context)
        val pi: PendingIntent = PendingIntent.getActivity(
            context, LOCATION_PERMISSION_REQUEST_CODE,
            actionIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Builder(context, ISSUES_CHANNEL_KEY)
            .setContentTitle("Location Permission Denied")
            .setTicker("Location Permission Denied")
            .setContentText("Grant location permission to Fairmatic Test")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOnlyAlertOnce(true)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    /**
     * Creates a notification to be displayed when activity recognition permission is denied to the application.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getActivityRecognitionPermissionDeniedNotification(context: Context, actionIntent: Intent): Notification {
        createNotificationChannels(context)
        val pi: PendingIntent = PendingIntent.getActivity(
            context, ACTIVITY_RECOGNITION_REQUEST,
            actionIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Builder(context, ISSUES_CHANNEL_KEY)
            .setContentTitle("Activity Recognition Denied")
            .setTicker("Activity Recognition Denied")
            .setContentText("Grant activity recognition permission to Fairmatic Test")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOnlyAlertOnce(true)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    /**
     * Creates a notification to be displayed when high accuracy location is disabled on the device.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocationDisabledNotification(context: Context): Notification {
        createNotificationChannels(context)
        val actionIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pi: PendingIntent = PendingIntent.getActivity(
            context, LOCATION_DISABLED_REQUEST_CODE,
            actionIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Builder(context, ISSUES_CHANNEL_KEY)
            .setContentTitle("Location Disabled")
            .setTicker("Location Disabled")
            .setContentIntent(pi)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentText("Enable settings for location.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    /**
     * Creates a notification to be displayed when a Google Play Settings Error
     * is reported by the Fairmatic SDK.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getGooglePlaySettingsNotification(
        context: Context,
        result: LocationSettingsResult?
    ): Notification {
        createNotificationChannels(context)
        val actionIntent = Intent(context, SettingsCheckActivity::class.java)
        actionIntent.action = Constants.EVENT_GOOGLE_PLAY_SETTING_ERROR
        actionIntent.putExtra("DATA", result)
        actionIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pi: PendingIntent = PendingIntent.getActivity(
            context, GOOGLE_PLAY_SETTINGS_REQUEST_CODE,
            actionIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Builder(context, ISSUES_CHANNEL_KEY)
            .setContentTitle("Google Play Settings Error")
            .setTicker("Google Play Settings Error")
            .setContentText("Tap here to resolve.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOnlyAlertOnce(true)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    /**
     * Creates a notification to be displayed when wifi scanning is disabled on the device.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getWifiScanningDisabledNotification(context: Context): Notification {
        createNotificationChannels(context)
        val actionIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pi: PendingIntent = PendingIntent.getActivity(
            context, WIFI_SCANNING_REQUEST_CODE,
            actionIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Builder(context, ISSUES_CHANNEL_KEY)
            .setContentTitle("Wifi Scanning Disabled")
            .setTicker("Wifi Scanning Disabled")
            .setContentText("Tap to enable wifi radio.")
            .setOnlyAlertOnce(true)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    fun displayFairmaticSetupFailureNotification(context: Context) {
        createNotificationChannels(context)
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(Constants.NOTIFICATION_ID, Fairmatic_FAILED_NOTIFICATION_ID)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val icon: Bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.notification_icon)
        val notifBuilder: androidx.core.app.NotificationCompat.Builder =
            androidx.core.app.NotificationCompat.Builder(context.applicationContext, ISSUES_CHANNEL_KEY)
                .setContentTitle("Failed To Enable Insurance Benefits")
                .setTicker("Failed To Enable Insurance Benefits")
                .setContentText("Tap This Notification To Retry")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
        notifBuilder.setCategory(Notification.CATEGORY_ERROR)
        val notification: Notification = notifBuilder.build()

        // Display notification
        val notificationManager: NotificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.notify(Fairmatic_FAILED_NOTIFICATION_ID, notification)
    }

    fun hideFairmaticSetupFailureNotification(context: Context) {
        val notificationManager: NotificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.cancel(Fairmatic_FAILED_NOTIFICATION_ID)
    }

    fun clearAllErrorNotifications(context: Context) {
        val manager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(LOCATION_PERMISSION_DENIED_NOTIFICATION_ID)
        manager.cancel(PSM_ENABLED_NOTIFICATION_ID)
        manager.cancel(BACKGROUND_RESTRICTED_NOTIFICATION_ID)
        manager.cancel(WIFI_SCANNING_DISABLED_NOTIFICATION_ID)
        manager.cancel(GOOGLE_PLAY_SETTINGS_NOTIFICATION_ID)
    }

    private fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager: NotificationManager? = context.getSystemService<NotificationManager>(
                NotificationManager::class.java
            )
            val foregroundNotificationChannel = NotificationChannel(
                FOREGROUND_CHANNEL_KEY, "Fairmatic trip tracking",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            foregroundNotificationChannel.setShowBadge(false)
            val issuesNotificationChannel = NotificationChannel(
                ISSUES_CHANNEL_KEY, "Issues",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            issuesNotificationChannel.setShowBadge(true)
            if (manager != null) {
                manager.createNotificationChannel(foregroundNotificationChannel)
                manager.createNotificationChannel(issuesNotificationChannel)
            }
        }
    }
}