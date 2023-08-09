package com.fairmatic.sampleapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.fairmatic.sampleapp.utils.NotificationUtility
import com.fairmatic.sdk.classes.FairmaticNotificationContainer
import com.fairmatic.sdk.classes.FairmaticNotificationProvider
import com.zendrive.sdk.ZendriveNotificationContainer

class MyFairmaticNotificationProvider : FairmaticNotificationProvider{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getInDriveNotificationContainer(context: Context): FairmaticNotificationContainer {
        return FairmaticNotificationContainer(
            MyFairmaticNotificationProvider.FAIRMATIC_NOTIFICATION_ID,
            NotificationUtility.getInDriveNotification(context)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getMaybeInDriveNotificationContainer(context: Context): FairmaticNotificationContainer {
        return FairmaticNotificationContainer(
            MyFairmaticNotificationProvider.FAIRMATIC_NOTIFICATION_ID,
            NotificationUtility.getMaybeInDriveNotification(context)
        )
    }

    override fun getWaitingForDriveNotificationContainer(context: Context): FairmaticNotificationContainer? {
        TODO("Not yet implemented")
    }

    companion object {
        private const val FAIRMATIC_NOTIFICATION_ID = 495
    }
}