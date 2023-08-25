package com.fairmatic.sampleapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fairmatic.sampleapp.manager.TripManager
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sampleapp.MainActivity
import com.fairmatic.sampleapp.R
import com.fairmatic.sampleapp.manager.FairmaticManager
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult

class OnDutyFragment : Fragment() {
    private lateinit var currentStateTextView: TextView
    private lateinit var currentInsurancePeriodTextView: TextView
    private lateinit var pickupAPassengerButton: Button
    private lateinit var cancelRequestButton: Button
    private lateinit var dropAPassengerButton: Button
    private lateinit var offDutyButton: Button
    private lateinit var acceptNewRideReqButton: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout: View = inflater.inflate(R.layout.fragment_onduty, container, false)
        currentStateTextView = layout.findViewById(R.id.currentStateTextView)
        currentInsurancePeriodTextView = layout.findViewById(R.id.currentInsurancePeriodTextView)
        //layout.findViewById<View>(R.id.acceptNewRideReqButton).setOnClickListener(this)
        acceptNewRideReqButton = layout.findViewById(R.id.acceptNewRideReqButton)
        //acceptNewRideReqButton.setOnClickListener(this)
        pickupAPassengerButton = layout.findViewById(R.id.pickupAPassengerButton)
        //pickupAPassengerButton.setOnClickListener(this)
        cancelRequestButton = layout.findViewById(R.id.cancelRequestButton)
        //cancelRequestButton.setOnClickListener(this)
        dropAPassengerButton = layout.findViewById(R.id.dropAPassengerButton)
        //dropAPassengerButton.setOnClickListener(this)
        offDutyButton = layout.findViewById(R.id.offDutyButton)
        //offDutyButton.setOnClickListener(this)
        //refreshUI()
        return layout
    }

    override fun onResume() {
        super.onResume()
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context)

        //create a clicklistener for the acceptNewRideReqButton
        acceptNewRideReqButton.setOnClickListener {
            Log.d(Constants.LOG_TAG_DEBUG, "acceptNewRideReqButton tapped")
            Toast.makeText(
                context,
                "Accepting new passenger request",
                Toast.LENGTH_SHORT
            ).show()
            val tripManager: TripManager? = context?.let { TripManager.sharedInstance(it) }
            if (tripManager != null) {
                refreshUIForPeriod2()
                context?.let { tripManager.acceptNewPassengerRequest(it, object :
                    FairmaticOperationCallback {
                    override fun onCompletion(fairmaticOperationResult: FairmaticOperationResult) {
                        if (fairmaticOperationResult is FairmaticOperationResult.Success) {
                            Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 2")
                        }
                        if (fairmaticOperationResult is FairmaticOperationResult.Failure) {
                            Log.d(
                                Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                        fairmaticOperationResult.error.name
                            )
                        }
                    }
                    }
                )
                }
            }
        }

        //create a click listener for the pickupAPassengerButton
        pickupAPassengerButton.setOnClickListener {
            Log.d(Constants.LOG_TAG_DEBUG, "pickupAPassengerButton tapped")
            val tripManager: TripManager? = context?.let { TripManager.sharedInstance(it) }
            if (tripManager != null) {
                refreshUIForPeriod3()
                context?.let { tripManager.pickupAPassenger(it, object :
                    FairmaticOperationCallback {
                    override fun onCompletion(fairmaticOperationResult: FairmaticOperationResult) {
                        if (fairmaticOperationResult is FairmaticOperationResult.Success) {
                            Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 3")
                        }
                        if (fairmaticOperationResult is FairmaticOperationResult.Failure) {
                            Log.d(
                                Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                        fairmaticOperationResult.error.name
                            )
                        }
                    }
                    }
                )
                }
            }
        }

        //create a click listener for the cancelRequestButton
        cancelRequestButton.setOnClickListener {
            Log.d(Constants.LOG_TAG_DEBUG, "cancelRequestButton tapped")
            val tripManager: TripManager? = context?.let { TripManager.sharedInstance(it) }
            if (tripManager != null) {
                refreshUIForPeriod1()
                context?.let { tripManager.cancelARequest(it, object :
                    FairmaticOperationCallback {
                    override fun onCompletion(fairmaticOperationResult: FairmaticOperationResult) {
                        if (fairmaticOperationResult is FairmaticOperationResult.Success) {
                            Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 1")
                        }
                        if (fairmaticOperationResult is FairmaticOperationResult.Failure) {
                            Log.d(
                                Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                        fairmaticOperationResult.error.name
                            )
                        }
                    }
                    }
                )
                }
            }
        }

        //create a click listener for the dropAPassengerButton
        dropAPassengerButton.setOnClickListener {
            Log.d(Constants.LOG_TAG_DEBUG, "dropAPassengerButton tapped")
            val tripManager: TripManager? = context?.let { TripManager.sharedInstance(it) }
            if (tripManager != null) {
                refreshUIForPeriod1()
                context?.let { tripManager.dropAPassenger(it, object :
                    FairmaticOperationCallback {
                    override fun onCompletion(fairmaticOperationResult: FairmaticOperationResult) {
                        if (fairmaticOperationResult is FairmaticOperationResult.Success) {
                            Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 1")
                        }
                        if (fairmaticOperationResult is FairmaticOperationResult.Failure) {
                            Log.d(
                                Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                        fairmaticOperationResult.error.name
                            )
                        }
                    }
                    }
                )
                }
            }
        }

        //create a click listener for the offDutyButton
        offDutyButton.setOnClickListener {
            Log.d(Constants.LOG_TAG_DEBUG, "offDutyButton tapped")
            Toast.makeText(
                context,
                "Going off duty",
                Toast.LENGTH_SHORT
            ).show()
            val tripManager: TripManager? = context?.let { TripManager.sharedInstance(it) }
            if (tripManager != null) {
                (activity as MainActivity).replaceFragment(OffDutyFragment())
                context?.let { tripManager.goOffDuty(it, object : FairmaticOperationCallback {
                    override fun onCompletion(fairmaticOperationResult: FairmaticOperationResult) {
                        if (fairmaticOperationResult is FairmaticOperationResult.Success) {
                            Log.d(Constants.LOG_TAG_DEBUG, "Insurance period stopped")
                        }
                        if (fairmaticOperationResult is FairmaticOperationResult.Failure) {
                            Log.d(
                                Constants.LOG_TAG_DEBUG, "Going Off duty failed, error: " +
                                        fairmaticOperationResult.error.name
                            )
                        }
                    }
                }) }
            }

        }
    }

    private fun refreshUIForPeriod1() {
        val tripManagerState: TripManager.State =
            context?.let { TripManager.sharedInstance(it)?.tripManagerState } ?: return
        val passengerInCar: Boolean = tripManagerState.passengerInCar
        val passengerWaitingForPickup: Boolean = tripManagerState.passengerWaitingForPickup
        var insurancePeriod = FairmaticManager.sharedInstance().currentlyActiveInsurancePeriod(context)?.insurancePeriod
        currentStateTextView.text = String.format(
            """
                Passenger In Car:  %s
                Passenger Waiting For Pickup:  %s
                """.trimIndent(), passengerInCar.toString().toUpperCase(),
            passengerWaitingForPickup.toString().toUpperCase()
        )
        currentInsurancePeriodTextView.text = String.format("Insurance Period: %d", insurancePeriod)
        acceptNewRideReqButton.isEnabled = true
        pickupAPassengerButton.isEnabled = false
        cancelRequestButton.isEnabled = false
        dropAPassengerButton.isEnabled = false
        offDutyButton.isEnabled = true
    }

    private fun refreshUIForPeriod2() {
        val tripManagerState: TripManager.State =
            context?.let { TripManager.sharedInstance(it)?.tripManagerState } ?: return
        val passengerInCar: Boolean = tripManagerState.passengerInCar
        val passengerWaitingForPickup: Boolean = tripManagerState.passengerWaitingForPickup
        var insurancePeriod = FairmaticManager.sharedInstance().currentlyActiveInsurancePeriod(context)?.insurancePeriod
        currentStateTextView.text = String.format(
            """
                Passenger In Car:  %s
                Passenger Waiting For Pickup:  %s
                """.trimIndent(), passengerInCar.toString().toUpperCase(),
            passengerWaitingForPickup.toString().toUpperCase()
        )
        currentInsurancePeriodTextView.text = String.format("Insurance Period: %d", insurancePeriod)
        acceptNewRideReqButton.isEnabled = false
        pickupAPassengerButton.isEnabled = true
        cancelRequestButton.isEnabled = true
        dropAPassengerButton.isEnabled = false
        offDutyButton.isEnabled = false
    }

    private fun refreshUIForPeriod3() {
        val tripManagerState: TripManager.State =
            context?.let { TripManager.sharedInstance(it)?.tripManagerState } ?: return
        val passengerInCar: Boolean = tripManagerState.passengerInCar
        val passengerWaitingForPickup: Boolean = tripManagerState.passengerWaitingForPickup
        var insurancePeriod = FairmaticManager.sharedInstance().currentlyActiveInsurancePeriod(context)?.insurancePeriod
        currentStateTextView.text = String.format(
            """
                Passenger In Car:  %s
                Passenger Waiting For Pickup:  %s
                """.trimIndent(), passengerInCar.toString().toUpperCase(),
            passengerWaitingForPickup.toString().toUpperCase()
        )
        currentInsurancePeriodTextView.text = String.format("Insurance Period: %d", insurancePeriod)
        acceptNewRideReqButton.isEnabled = false
        pickupAPassengerButton.isEnabled = false
        cancelRequestButton.isEnabled = false
        dropAPassengerButton.isEnabled = true
        offDutyButton.isEnabled = false
    }
}