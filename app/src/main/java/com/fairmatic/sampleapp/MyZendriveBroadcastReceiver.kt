package com.fairmatic.sampleapp

import android.content.Context
import android.util.Log
import com.fairmatic.sampleapp.manager.SharedPrefsManager
import com.fairmatic.sampleapp.manager.ZendriveManager
import com.zendrive.sdk.AccidentInfo
import com.zendrive.sdk.AnalyzedDriveInfo
import com.zendrive.sdk.DriveResumeInfo
import com.zendrive.sdk.DriveStartInfo
import com.zendrive.sdk.EstimatedDriveInfo
import com.zendrive.sdk.ZendriveBroadcastReceiver

class MyZendriveBroadcastReceiver : ZendriveBroadcastReceiver() {
    override fun onDriveStart(context: Context?, driveStartInfo: DriveStartInfo?) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveStart")
    }

    override fun onDriveEnd(context: Context?, estimatedDriveInfo: EstimatedDriveInfo?) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveEnd")
    }

    override fun onDriveAnalyzed(context: Context?, analyzedDriveInfo: AnalyzedDriveInfo?) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveAnalyzed")
    }

    override fun onDriveResume(context: Context?, driveResumeInfo: DriveResumeInfo?) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveResume")
    }

    override fun onAccident(context: Context?, accidentInfo: AccidentInfo?) {
        Log.d(Constants.LOG_TAG_DEBUG, "onAccident")
    }

    override fun onZendriveSettingsConfigChanged(
        context: Context?, errorsFound: Boolean,
        warningsFound: Boolean
    ) {
        Log.d(Constants.LOG_TAG_DEBUG, "onZendriveSettingsConfigChanged")

        // Persist whether the Zendrive SDK has detected errors or warnings.
        // Use these persisted flags as a basis to determine whether Zendrive settings
        // should be fetched on app resume.
        val prefsManager: SharedPrefsManager? = context?.let { SharedPrefsManager.sharedInstance(it) }
        prefsManager?.setSettingsErrorsFound(errorsFound)
        if (prefsManager != null) {
            prefsManager.isSettingsWarningsFound= warningsFound
        }
        if (context != null) {
            ZendriveManager.sharedInstance().checkZendriveSettings(context)
        }
    }
}