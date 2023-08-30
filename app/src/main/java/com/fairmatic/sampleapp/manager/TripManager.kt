package com.fairmatic.sampleapp.manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult

class TripManager private constructor(context: Context) {
    inner class State {
        var isUserOnDuty: Boolean
        var passengerWaitingForPickup: Boolean
        var passengerInCar: Boolean
        private var trackingId: String?

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

    private val state: State?

    init {
        val sharedPrefsManager = SharedPrefsManager.sharedInstance(context)
        state = sharedPrefsManager?.let{
            State(
                it.isUserOnDuty,
                it.passengersInCar,
                it.passengersWaitingForPickup,
                it.trackingId
            )
        }
    }

    @Synchronized
    fun acceptNewPassengerRequest(context: Context, callback: FairmaticOperationCallback) {
        FairmaticManager.sharedInstance().handleInsurancePeriod2(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    state?.passengerWaitingForPickup = true
                    SharedPrefsManager.sharedInstance(context)
                        ?.passengersWaitingForPickup = state?.passengerWaitingForPickup == true
                } else {
                    Toast.makeText(context, "Failed to accept new passenger request", Toast.LENGTH_SHORT).show()
                }
                callback.onCompletion(result)
            }
        })
    }



    @Synchronized
    fun pickupAPassenger(context: Context, callback: FairmaticOperationCallback) {
        FairmaticManager.sharedInstance().handleInsurancePeriod3(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    state?.passengerInCar = true
                    state?.passengerWaitingForPickup = false
                    SharedPrefsManager.sharedInstance(context)?.passengersInCar = state?.passengerInCar == true
                    SharedPrefsManager.sharedInstance(context)?.passengersWaitingForPickup = state?.passengerWaitingForPickup == true

                } else {
                    Toast.makeText(context, "Failed to pickup a passenger", Toast.LENGTH_SHORT).show()
                }
                callback.onCompletion(result)
            }
        })
    }

    @Synchronized
    fun cancelARequest(context: Context, callback: FairmaticOperationCallback) {
        FairmaticManager.sharedInstance().handleInsurancePeriod1(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    state?.passengerWaitingForPickup = false
                    SharedPrefsManager.sharedInstance(context)
                        ?.passengersWaitingForPickup = state?.passengerWaitingForPickup == true
                } else {
                    Toast.makeText(context, "Failed to cancel a request", Toast.LENGTH_SHORT).show()
                }
                callback.onCompletion(result)
            }
        })
    }

    @Synchronized
    fun dropAPassenger(context: Context, callback: FairmaticOperationCallback) {
        FairmaticManager.sharedInstance().handleInsurancePeriod1(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    state?.passengerInCar = false
                    SharedPrefsManager.sharedInstance(context)?.passengersInCar = state?.passengerInCar == true
                } else {
                    Toast.makeText(context, "Failed to drop a passenger", Toast.LENGTH_SHORT).show()
                }
                callback.onCompletion(result)
            }
        })
    }

    @Synchronized
    fun goOnDuty(context: Context, callback: FairmaticOperationCallback) {
        FairmaticManager.sharedInstance().handleInsurancePeriod1(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    Log.d(Constants.LOG_TAG_DEBUG, "goOnDuty of tripmanager called")
                    state?.isUserOnDuty = true
                    SharedPrefsManager.sharedInstance(context)?.isUserOnDuty = state?.isUserOnDuty == true
                } else {
                    Toast.makeText(context, "Failed to go on duty", Toast.LENGTH_SHORT).show()
                }
                callback.onCompletion(result)
            }
        })
    }

    @Synchronized
    fun goOffDuty(context: Context, callback: FairmaticOperationCallback) {
        FairmaticManager.sharedInstance().handleStopPeriod(context, object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success){
                    state?.isUserOnDuty = false
                    SharedPrefsManager.sharedInstance(context)?.isUserOnDuty = state?.isUserOnDuty == true
                } else {
                    Toast.makeText(context, "Failed to go off duty", Toast.LENGTH_SHORT).show()
                }
                callback.onCompletion(result)

            }
        })
    }

    @get:Synchronized
    val tripManagerState: State?
        get() = state?.let { State(it) }

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