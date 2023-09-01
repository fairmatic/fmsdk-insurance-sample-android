package com.fairmatic.sampleapp.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

@SuppressLint("ApplySharedPref")
class SharedPrefsManager private constructor(context: Context) {
    private val prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences("com.fairmatic.sampleapp", Context.MODE_PRIVATE)
    }

    var driverId: String? = null
        get() = prefs.getString(DRIVER_ID, null)
        set(value) {
            field = value
            prefs.edit().putString(DRIVER_ID, value).apply()
        }
    var isUserOnDuty: Boolean = false
        get() = prefs.getBoolean(USER_ON_DUTY, false)
        set(value) {
            field = value
            prefs.edit().putBoolean(USER_ON_DUTY, value).apply()
        }

    var passengersInCar: Boolean = false
        get() = prefs.getBoolean(PASSENGERS_IN_CAR, false)
        set(value) {
            field = value
            prefs.edit().putBoolean(PASSENGERS_IN_CAR, value).apply()
        }

    var passengersWaitingForPickup: Boolean = false
        get() = prefs.getBoolean(PASSENGERS_WAITING_FOR_PICKUP, false)
        set(value) {
            field = value
            prefs.edit().putBoolean(PASSENGERS_WAITING_FOR_PICKUP, value).apply()
        }


    var trackingId: String? = null
        get() = prefs.getString(TRACKING_ID, null)
        set(value) {
            field = value
            prefs.edit().putString(TRACKING_ID, value).apply()
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