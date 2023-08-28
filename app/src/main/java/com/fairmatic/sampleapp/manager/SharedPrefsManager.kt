package com.fairmatic.sampleapp.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.lang.reflect.Array.set

@SuppressLint("ApplySharedPref")
class SharedPrefsManager private constructor(context: Context) {
    private val prefs: SharedPreferences

    init {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    var driverId: String?
        get() = prefs.getString(DRIVER_ID, null)
        set(driverId) {
            prefs.edit().putString(DRIVER_ID, driverId).apply()
        }
    var isUserOnDuty: Boolean
        get() = prefs.getBoolean(USER_ON_DUTY, false)
        set(isUserOnDuty) {
            prefs.edit().putBoolean(USER_ON_DUTY, isUserOnDuty).apply()
        }

    var passengersInCar: Boolean
        get() = prefs.getBoolean(PASSENGERS_IN_CAR, false)
        set(passengersInCar) {
            prefs.edit().putBoolean(PASSENGERS_IN_CAR, passengersInCar).apply()
        }

    var passengersWaitingForPickup: Boolean
        get() = prefs.getBoolean(PASSENGERS_WAITING_FOR_PICKUP, false)
        set(passengersWaitingForPickup) {
            prefs.edit().putBoolean(PASSENGERS_WAITING_FOR_PICKUP, passengersWaitingForPickup).apply()
        }


    var trackingId: String?
        get() = prefs.getString(TRACKING_ID, null)
        set(trackingId) {
            prefs.edit().putString(TRACKING_ID, trackingId).apply()
        }
    val isSettingsErrorFound: Boolean
        get() = prefs.getBoolean(FAIRMATIC_SETTINGS_ERRORS, false)

    fun setSettingsErrorsFound(errorsFound: Boolean) {
        prefs.edit().putBoolean(FAIRMATIC_SETTINGS_ERRORS, errorsFound).apply()
    }

    var isSettingsWarningsFound: Boolean
        get() = prefs.getBoolean(FAIRMATIC_SETTINGS_WARNINGS, false)
        set(warningsFound) {
            prefs.edit().putBoolean(FAIRMATIC_SETTINGS_WARNINGS, warningsFound).apply()
        }

    fun shouldRetryFairmaticSetup(): Boolean {
        return prefs.getBoolean(RETRY_FAIRMATIC_SETUP, false)
    }

    fun setRetryFairmaticSetup(retry: Boolean) {
        prefs.edit().putBoolean(RETRY_FAIRMATIC_SETUP, retry).apply()
    }

    companion object {
        private var sharedInstance: SharedPrefsManager? = null
        private const val DRIVER_ID = "driverId"
        private const val USER_ON_DUTY = "isUserOnDuty"
        private const val PASSENGERS_IN_CAR = "passengersInCar"
        private const val PASSENGERS_WAITING_FOR_PICKUP = "passengersWaitingForPickup"
        private const val TRACKING_ID = "trackingId"
        private const val FAIRMATIC_SETTINGS_ERRORS = "errorsFound"
        private const val FAIRMATIC_SETTINGS_WARNINGS = "warningsFound"
        private const val RETRY_FAIRMATIC_SETUP = "retry_fairmatic_setup"
        @JvmStatic
        @Synchronized
        fun sharedInstance(context: Context): SharedPrefsManager? {
            if (sharedInstance == null) {
                sharedInstance = SharedPrefsManager(context)
            }
            return sharedInstance
        }
    }
}