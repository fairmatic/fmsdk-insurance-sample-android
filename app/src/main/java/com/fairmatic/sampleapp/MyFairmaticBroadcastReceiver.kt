package com.fairmatic.sampleapp

import android.content.Context
import android.util.Log
import com.fairmatic.sampleapp.manager.FairmaticManager
import com.fairmatic.sampleapp.manager.SharedPrefsManager
import com.fairmatic.sdk.classes.AccidentInfo
import com.fairmatic.sdk.classes.AnalyzedDriveInfo
import com.fairmatic.sdk.classes.DriveResumeInfo
import com.fairmatic.sdk.classes.DriveStartInfo
import com.fairmatic.sdk.classes.EstimatedDriveInfo
import com.fairmatic.sdk.classes.FairmaticBroadcastReceiver

class MyFairmaticBroadcastReceiver : FairmaticBroadcastReceiver() {
    override fun onDriveStart(context: Context, driveStartInfo: DriveStartInfo?) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveStart")
    }

    override fun onDriveEnd(context: Context, estimatedDriveInfo: EstimatedDriveInfo?) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveEnd")
    }

    override fun onDriveAnalyzed(context: Context, analyzedDriveInfo: AnalyzedDriveInfo?) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveAnalyzed")
    }

    override fun onDriveResume(context: Context, driveResumeInfo: DriveResumeInfo?) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveResume")
    }

    override fun onAccident(context: Context, accidentInfo: AccidentInfo?) {
        Log.d(Constants.LOG_TAG_DEBUG, "onAccident")
    }

    override fun onFairmaticSettingsConfigChanged(
        context: Context,
        errorsFound: Boolean,
        warningsFound: Boolean
    ) {
        Log.d(Constants.LOG_TAG_DEBUG, "onFairmaticConfigChanged called")
        if (errorsFound || warningsFound) {
            FairmaticManager.checkFairmaticSettings(context)
        }
    }
}