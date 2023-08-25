package com.fairmatic.sampleapp.manager

import android.content.Context
import android.widget.Toast
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult

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
            this.passengerWaitingForPickup = passenegersWaitingForPickup
            this.passengerInCar = passenegersInCar
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
    fun acceptNewPassengerRequest(context: Context, callback: FairmaticOperationCallback) {
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().handleInsurancePeriod2(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    state.passengerWaitingForPickup = true
                    SharedPrefsManager.sharedInstance(context)
                        ?.setPassengersWaitingForPickup(state.passengerWaitingForPickup)
                } else {
                    Toast.makeText(context, "Failed to accept new passenger request", Toast.LENGTH_SHORT).show()
                }
                callback.onCompletion(result)
            }
        })
    }



    @Synchronized
    fun pickupAPassenger(context: Context, callback: FairmaticOperationCallback) {
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().handleInsurancePeriod3(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    state.passengerInCar = true
                    state.passengerWaitingForPickup = false
                    SharedPrefsManager.sharedInstance(context)!!.setPassengersInCar(state.passengerInCar)

                } else {
                    Toast.makeText(context, "Failed to pickup a passenger", Toast.LENGTH_SHORT).show()
                }
                callback.onCompletion(result)
            }
        })
    }

    @Synchronized
    fun cancelARequest(context: Context, callback: FairmaticOperationCallback) {
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().handleInsurancePeriod1(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    state.passengerWaitingForPickup = false
                    SharedPrefsManager.sharedInstance(context)
                        ?.setPassengersWaitingForPickup(state.passengerWaitingForPickup)
                } else {
                    Toast.makeText(context, "Failed to cancel a request", Toast.LENGTH_SHORT).show()
                }
                callback.onCompletion(result)
            }
        })
    }

    @Synchronized
    fun dropAPassenger(context: Context, callback: FairmaticOperationCallback) {
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().handleInsurancePeriod1(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    state.passengerInCar = false
                    SharedPrefsManager.sharedInstance(context)!!.setPassengersInCar(state.passengerInCar)
                } else {
                    Toast.makeText(context, "Failed to drop a passenger", Toast.LENGTH_SHORT).show()
                }
                callback.onCompletion(result)
            }
        })
    }

    @Synchronized
    fun goOnDuty(context: Context) {
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().handleInsurancePeriod1(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    state.isUserOnDuty = true
                    SharedPrefsManager.sharedInstance(context)?.isUserOnDuty = state.isUserOnDuty
                } else {
                    Toast.makeText(context, "Failed to go on duty", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    @Synchronized
    fun goOffDuty(context: Context, callback: FairmaticOperationCallback) {
        updateTrackingIdIfNeeded(context)
        FairmaticManager.sharedInstance().handleStopPeriod(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    state.isUserOnDuty = false
                    SharedPrefsManager.sharedInstance(context)?.isUserOnDuty = state.isUserOnDuty
                } else {
                    Toast.makeText(context, "Failed to go off duty", Toast.LENGTH_SHORT).show()
                }
                callback.onCompletion(result)

            }
        })
    }

    private fun updateTrackingIdIfNeeded(context: Context) {
        if (state.isUserOnDuty ) {
            // We need trackingId
            if (state.trackingId == null) {
                state.trackingId = System.currentTimeMillis().toString()
                SharedPrefsManager.sharedInstance(context)?.trackingId = state.trackingId
            }
        } else {
            state.trackingId = null
            SharedPrefsManager.sharedInstance(context)?.trackingId = state.trackingId
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