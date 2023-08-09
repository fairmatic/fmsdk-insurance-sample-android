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

class OnDutyFragment : Fragment(), View.OnClickListener {
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
        acceptNewRideReqButton.setOnClickListener(this)
        pickupAPassengerButton = layout.findViewById(R.id.pickupAPassengerButton)
        pickupAPassengerButton.setOnClickListener(this)
        cancelRequestButton = layout.findViewById(R.id.cancelRequestButton)
        cancelRequestButton.setOnClickListener(this)
        dropAPassengerButton = layout.findViewById(R.id.dropAPassengerButton)
        dropAPassengerButton.setOnClickListener(this)
        offDutyButton = layout.findViewById(R.id.offDutyButton)
        offDutyButton.setOnClickListener(this)
        refreshUI()
        return layout
    }

    @SuppressLint("DefaultLocale")
    private fun refreshUI() {
        val tripManagerState: TripManager.State =
            context?.let { TripManager.sharedInstance(it)?.tripManagerState } ?: return
        val passengerInCar: Boolean = tripManagerState.passengerInCar
        val passengerWaitingForPickup: Boolean = tripManagerState.passengerWaitingForPickup
        var insurancePeriod = 0
        if (passengerInCar) {
            insurancePeriod = 3
        } else if (passengerWaitingForPickup) {
            insurancePeriod = 2
        } else if (tripManagerState.isUserOnDuty) {
            insurancePeriod = 1
        }
        currentStateTextView.text = String.format(
            """
                Passenger In Car:  %s
                Passenger Waiting For Pickup:  %s
                """.trimIndent(), passengerInCar.toString().toUpperCase(),
            passengerWaitingForPickup.toString().toUpperCase()
        )
        currentInsurancePeriodTextView.text = String.format("Insurance Period: %d", insurancePeriod)

        acceptNewRideReqButton.isEnabled  = !passengerWaitingForPickup && !passengerInCar
        pickupAPassengerButton.isEnabled = passengerWaitingForPickup == true
        cancelRequestButton.isEnabled = passengerWaitingForPickup == true
        dropAPassengerButton.isEnabled = passengerInCar == true
        offDutyButton.isEnabled = !passengerInCar && !passengerWaitingForPickup
    }

    override fun onClick(view: View) {
        val tripManager: TripManager? = context?.let { TripManager.sharedInstance(it) }
        when (view.id) {
            R.id.acceptNewRideReqButton -> {
                Log.d(Constants.LOG_TAG_DEBUG, "acceptNewRideReqButton tapped")
                Toast.makeText(
                    context,
                    "Accepting new passenger request",
                    Toast.LENGTH_SHORT
                ).show()
                if (tripManager != null) {
                    context?.let { tripManager.acceptNewPassengerRequest(it) }
                }
            }

            R.id.pickupAPassengerButton -> {
                Log.d(Constants.LOG_TAG_DEBUG, "pickupAPassengerButton tapped")
                if (tripManager != null) {
                    context?.let { tripManager.pickupAPassenger(it) }
                }
            }

            R.id.cancelRequestButton -> {
                Log.d(Constants.LOG_TAG_DEBUG, "cancelRequestButton tapped")
                if (tripManager != null) {
                    context?.let { tripManager.cancelARequest(it) }
                }
            }

            R.id.dropAPassengerButton -> {
                Log.d(Constants.LOG_TAG_DEBUG, "dropAPassengerButton tapped")
                if (tripManager != null) {
                    context?.let { tripManager.dropAPassenger(it) }
                }
            }

            R.id.offDutyButton -> {
                Log.d(Constants.LOG_TAG_DEBUG, "offDutyButton tapped")
                Toast.makeText(
                    context,
                    "Going off duty",
                    Toast.LENGTH_SHORT
                ).show()
                if (tripManager != null) {
                    context?.let { tripManager.goOffDuty(it) }
                }
                (activity as MainActivity).replaceFragment(OffDutyFragment())
            }
        }
        refreshUI()
    }
}