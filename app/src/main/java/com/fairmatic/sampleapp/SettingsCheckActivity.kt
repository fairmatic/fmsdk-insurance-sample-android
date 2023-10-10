package com.fairmatic.sampleapp

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.fairmatic.sampleapp.utils.NotificationUtility
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes

class SettingsCheckActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resolveSettingsErrors()
    }

    private fun resolveSettingsErrors() {
        val intent: Intent = intent
        if (intent.action == null) {
            return
        }
        if (Constants.EVENT_LOCATION_PERMISSION_ERROR == intent.action) {
            requestLocationPermission()
        }
        if (Constants.EVENT_GOOGLE_PLAY_SETTING_ERROR == intent.action) {
            val r: LocationSettingsResult? = intent.getParcelableExtra("DATA")
            if (r != null) {
                resolveGooglePlaySettings(r)
            }
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            Constants.LOCATION_PERMISSION_REQUEST
        )
    }

    private fun resolveGooglePlaySettings(result: LocationSettingsResult) {
        val status: Status = result.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS ->                 // Should not happen
                Log.d(
                    Constants.LOG_TAG_DEBUG, "Success received when expected" +
                            "error from Google Play " +
                            "Services"
                )

            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                 // Location settings are not satisfied. But could be fixed by showing the user
                // a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                        this,
                        Constants.GOOGLE_PLAY_SERVICES_REQUEST_CHECK_SETTINGS
                    )
                } catch (e: IntentSender.SendIntentException) {
                    // Ignore the error.
                }

            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val notificationManager: NotificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.cancel(NotificationUtility.LOCATION_PERMISSION_DENIED_NOTIFICATION_ID)
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}