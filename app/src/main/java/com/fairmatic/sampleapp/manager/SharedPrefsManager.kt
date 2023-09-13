package com.fairmatic.sampleapp.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("ApplySharedPref")
class SharedPrefsManager private constructor(context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("com.fairmatic.sampleapp", Context.MODE_PRIVATE)
    }

    private val DRIVER_ID = "driverId"
    private val USER_ON_DUTY = "isUserOnDuty"
    private val PASSENGER_IN_CAR = "passengerInCar"
    private val PASSENGER_WAITING_FOR_PICKUP = "passengerWaitingForPickup"
    private val FAIRMATIC_SETTINGS_ERRORS = "errorsFound"
    private val FAIRMATIC_SETTINGS_WARNINGS = "warningsFound"

    var driverId: String? = null
        get() = prefs.getString(DRIVER_ID, "")
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

    var passengerWaitingForPickup: Boolean = false
        get() = prefs.getBoolean(PASSENGER_WAITING_FOR_PICKUP, false)
        set(value) {
            field = value
            prefs.edit().putBoolean(PASSENGER_WAITING_FOR_PICKUP, value).apply()
        }

    var passengerInCar: Boolean = false
        get() = prefs.getBoolean(PASSENGER_IN_CAR, false)
        set(value) {
            field = value
            prefs.edit().putBoolean(PASSENGER_IN_CAR, value).apply()
        }

    companion object {
        private lateinit var instance: SharedPrefsManager

        fun sharedInstance(context: Context): SharedPrefsManager {
            if (::instance.isInitialized.not()) {
                instance = SharedPrefsManager(context)
            }

            return instance
        }
    }
}