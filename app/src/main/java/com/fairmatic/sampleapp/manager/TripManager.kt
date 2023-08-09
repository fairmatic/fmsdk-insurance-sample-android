package com.fairmatic.sampleapp.manager

import android.content.Context

class TripManager private constructor(context: Context) {
    inner class State {
        var isUserOnDuty: Boolean
        var passengerWaitingForPickup: Boolean
        var passengerInCar: Boolean
        var trackingId: String?

        internal constructor(
            isUserOnDuty: Boolean, passenegersWaitingForPickup: Boolean,
            passenegersInCar: Boolean, trackingId: String?
        ) {
            this.isUserOnDuty = isUserOnDuty
            passengerWaitingForPickup = passenegersWaitingForPickup
            passengerInCar = passenegersInCar
            this.trackingId = trackingId
        }

        internal constructor(another: State) {
            isUserOnDuty = another.isUserOnDuty
            passengerWaitingForPickup = another.passengerWaitingForPickup
            passengerInCar = another.passengerInCar
            trackingId = another.trackingId
        }
    }

    private val state: State

    init {
        val sharedPrefsManager = SharedPrefsManager.sharedInstance(context)
        state = State(
            sharedPrefsManager!!.isUserOnDuty,
            sharedPrefsManager.passengersWaitingForPickup(),
            sharedPrefsManager.passengersInCar(),
            sharedPrefsManager.trackingId
        )
    }

    @Synchronized
    fun acceptNewPassengerRequest(context: Context) {
        state.passengerWaitingForPickup = true
        SharedPrefsManager.sharedInstance(context)
            ?.setPassengersWaitingForPickup(state.passengerWaitingForPickup)
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context)
    }

    @Synchronized
    fun pickupAPassenger(context: Context) {
        state.passengerWaitingForPickup = false
        SharedPrefsManager.sharedInstance(context)
            ?.setPassengersWaitingForPickup(state.passengerWaitingForPickup)
        state.passengerInCar = true
        SharedPrefsManager.sharedInstance(context)
            ?.setPassengersInCar(state.passengerInCar)
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context)
    }

    @Synchronized
    fun cancelARequest(context: Context) {
        state.passengerWaitingForPickup = false
        SharedPrefsManager.sharedInstance(context)
            ?.setPassengersWaitingForPickup(state.passengerWaitingForPickup)
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context)
    }

    @Synchronized
    fun dropAPassenger(context: Context) {
        state.passengerInCar = false
        SharedPrefsManager.sharedInstance(context)!!.setPassengersInCar(state.passengerInCar)
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context)
    }

    @Synchronized
    fun goOnDuty(context: Context) {
        state.isUserOnDuty = true
        SharedPrefsManager.sharedInstance(context)?.isUserOnDuty = state.isUserOnDuty
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context)
    }

    @Synchronized
    fun goOffDuty(context: Context) {
        state.isUserOnDuty = false
        SharedPrefsManager.sharedInstance(context)?.isUserOnDuty = state.isUserOnDuty
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context)
    }

    private fun updateTrackingIdIfNeeded(context: Context) {
        if (state.passengerWaitingForPickup || state.passengerInCar ) {
            // We need trackingId
            if (state.trackingId == null) {
                state.trackingId = System.currentTimeMillis().toString()
                SharedPrefsManager.sharedInstance(context)!!.trackingId = state.trackingId
            }
        } else {
            state.trackingId = null
            SharedPrefsManager.sharedInstance(context)!!.trackingId = state.trackingId
        }
    }

    @get:Synchronized
    val tripManagerState: State
        get() = State(state)

    companion object {
        private var sharedInstance: TripManager? = null
        @JvmStatic
        @Synchronized
        fun sharedInstance(context: Context): TripManager? {
            if (sharedInstance == null) {
                sharedInstance = TripManager(context)
            }
            return sharedInstance
        }
    }
}