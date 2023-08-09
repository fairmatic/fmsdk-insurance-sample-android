package com.fairmatic.sampleapp

import android.content.Context
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.fairmatic.sampleapp.utils.NotificationUtility
import com.zendrive.sdk.ZendriveNotificationContainer
import com.zendrive.sdk.ZendriveNotificationProvider

class MyZendriveNotificationProvider : ZendriveNotificationProvider {
    override fun getWaitingForDriveNotificationContainer(p0: Context): ZendriveNotificationContainer? {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @NonNull
    override fun getMaybeInDriveNotificationContainer(p0: Context): ZendriveNotificationContainer {
        return ZendriveNotificationContainer(
            ZENDRIVE_NOTIFICATION_ID,
            NotificationUtility.getMaybeInDriveNotification(p0)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @NonNull
    override fun getInDriveNotificationContainer(p0: Context): ZendriveNotificationContainer {
        return ZendriveNotificationContainer(
            ZENDRIVE_NOTIFICATION_ID,
            NotificationUtility.getInDriveNotification(p0)
        )
    }

    companion object {
        private const val ZENDRIVE_NOTIFICATION_ID = 495
    }


}