package com.fairmatic.sampleapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sampleapp.SettingsCheckActivity
import com.fairmatic.sdk.classes.FairmaticIssueType

object SettingIntent {
    fun forError(type: FairmaticIssueType, context: Context): Intent? {
        return when (type) {
            FairmaticIssueType.POWER_SAVER_MODE_ENABLED -> {
                Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
            }
            FairmaticIssueType.BACKGROUND_RESTRICTION_ENABLED -> {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
            }
            FairmaticIssueType.LOCATION_PERMISSION_DENIED,
            FairmaticIssueType.PRECISE_LOCATION_DENIED -> {
                Intent(context, SettingsCheckActivity::class.java).apply {
                    setAction(Constants.EVENT_LOCATION_PERMISSION_ERROR)
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            }
            FairmaticIssueType.BATTERY_OPTIMIZATION_ENABLED -> {
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
            }
            FairmaticIssueType.ACTIVITY_RECOGNITION_PERMISSION_DENIED -> {
                Intent(context, SettingsCheckActivity::class.java).apply {
                    setAction(Constants.EVENT_ACTIVITY_RECOGNITION_ERROR)
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            }
            else -> null
        }
    }
}