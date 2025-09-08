package com.fairmatic.sampleapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sampleapp.R
import com.fairmatic.sampleapp.manager.FairmaticManager
import com.fairmatic.sampleapp.manager.SharedPrefsManager
import com.fairmatic.sdk.Fairmatic
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult

class OnDutyFragment(val goOffDuty: () -> Unit) : Fragment() {

    private lateinit var currentStateTextView: TextView
    private lateinit var currentInsurancePeriodTextView: TextView
    private lateinit var pickupAPassengerButton: Button
    private lateinit var cancelRequestButton: Button
    private lateinit var dropAPassengerButton: Button
    private lateinit var openIncidentReportingWebPageButton: LinearLayout
    private lateinit var offDutyButton: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var acceptNewRideReqButton: Button

    private val sharedPrefsManager by lazy { SharedPrefsManager.sharedInstance(requireContext()) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout: View = inflater.inflate(R.layout.fragment_onduty, container, false)

        // Information text views
        currentStateTextView = layout.findViewById(R.id.currentStateTextView)
        currentInsurancePeriodTextView = layout.findViewById(R.id.currentInsurancePeriodTextView)

        // Buttons
        acceptNewRideReqButton = layout.findViewById(R.id.acceptNewRideRequestButton)
        acceptNewRideReqButton.setOnClickListener { acceptNewRideRequestClicked() }

        pickupAPassengerButton = layout.findViewById(R.id.pickupAPassengerButton)
        pickupAPassengerButton.setOnClickListener { pickUpAPassengerClicked() }

        cancelRequestButton = layout.findViewById(R.id.cancelARequestButton)
        cancelRequestButton.setOnClickListener { cancelARequestClicked() }

        dropAPassengerButton = layout.findViewById(R.id.dropAPassengerButton)
        dropAPassengerButton.setOnClickListener { dropAPassengerClicked() }

        openIncidentReportingWebPageButton = layout.findViewById(R.id.openIncidentReportButton)
        openIncidentReportingWebPageButton.setOnClickListener {
            Log.d(Constants.LOG_TAG_DEBUG, "Opening incident reporting URL")
            openIncidentReportingUrl() }

        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar)
        loadingProgressBar.visibility = View.GONE

        offDutyButton = layout.findViewById(R.id.offDutyButton)
        offDutyButton.setOnClickListener { offDutyClicked() }

        refreshUI()
        return layout
    }

    private fun acceptNewRideRequestClicked() {
        FairmaticManager.startInsurancePeriod2(requireContext(), object :
            FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success) {
                    Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 2")
                }
                if (result is FairmaticOperationResult.Failure) {
                    Log.d(
                        Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                result.error.name
                    )
                }
            }
        })

        sharedPrefsManager.passengerWaitingForPickup = true
        refreshUIForPeriod2()
    }

    private fun pickUpAPassengerClicked() {
        FairmaticManager.startInsurancePeriod3(requireContext(), object :
            FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success) {
                    Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 3")
                } else if (result is FairmaticOperationResult.Failure) {
                    Log.d(
                        Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                result.error.name
                    )
                }
            }
        })

        sharedPrefsManager.passengerInCar = true
        sharedPrefsManager.passengerWaitingForPickup = false
        refreshUIForPeriod3()
    }

    private fun cancelARequestClicked() {
        FairmaticManager.startInsurancePeriod1(requireContext(), object :
            FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success) {
                    Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 1")
                }
                if (result is FairmaticOperationResult.Failure) {
                    Log.d(
                        Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                result.error.name
                    )
                }
            }
        })

        sharedPrefsManager.passengerWaitingForPickup = true
        refreshUIForPeriod1()
    }

    private fun dropAPassengerClicked() {
        FairmaticManager.startInsurancePeriod1(requireContext(), object :
            FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success) {
                    Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 1")
                }
                if (result is FairmaticOperationResult.Failure) {
                    Log.d(
                        Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                result.error.name
                    )
                }
            }
        }
        )

        sharedPrefsManager.passengerInCar = false
        refreshUIForPeriod1()
    }

    private fun offDutyClicked() {
        Toast.makeText(context, "Going off duty", Toast.LENGTH_SHORT).show()

        FairmaticManager.stopPeriod(requireContext(), object : FairmaticOperationCallback {
            override fun onCompletion(result: FairmaticOperationResult) {
                if (result is FairmaticOperationResult.Success) {
                    Log.d(Constants.LOG_TAG_DEBUG, "Insurance period stopped")
                }
                if (result is FairmaticOperationResult.Failure) {
                    Log.d(
                        Constants.LOG_TAG_DEBUG, "Going Off duty failed, error: " +
                                result.error.name
                    )
                }
            }
        })

        sharedPrefsManager.isUserOnDuty = false
        goOffDuty()
    }

    private fun openIncidentReportingUrl() {
        loadingProgressBar.visibility = View.VISIBLE
        FairmaticManager.openIncidentReportingWebPage(
            requireContext(),
            object : FairmaticOperationCallback {
                override fun onCompletion(result: FairmaticOperationResult) {
                    loadingProgressBar.visibility = View.GONE
                    if (result is FairmaticOperationResult.Success) {
                        Log.d(Constants.LOG_TAG_DEBUG, "Incident reporting URL opened")
                    }
                    if (result is FairmaticOperationResult.Failure) {
                        Log.d(
                            Constants.LOG_TAG_DEBUG,
                            "Opening incident reporting URL failed, error: " +
                                    result.error.name
                        )
                    }
                }
            })
    }

    private fun refreshUI(){
        val sharedPrefsManager : SharedPrefsManager = SharedPrefsManager.sharedInstance(requireContext())
        if(sharedPrefsManager.passengerWaitingForPickup){
            refreshUIForPeriod2()
        } else if(sharedPrefsManager.passengerInCar){
            refreshUIForPeriod3()
        } else {
            refreshUIForPeriod1()
        }
    }

    private fun refreshUIForPeriod1() {
        currentStateTextView.text = String.format(
            """
                Passenger In Car:  FALSE
                Passenger Waiting For Pickup:  FALSE
                """.trimIndent()
        )
        currentInsurancePeriodTextView.text = String.format("Insurance Period: 1")
        acceptNewRideReqButton.isEnabled = true
        pickupAPassengerButton.isEnabled = false
        cancelRequestButton.isEnabled = false
        dropAPassengerButton.isEnabled = false
        offDutyButton.isEnabled = true
    }

    private fun refreshUIForPeriod2() {
        currentStateTextView.text = String.format(
            """
                Passenger In Car:  FALSE
                Passenger Waiting For Pickup:  TRUE
                """.trimIndent()
        )
        currentInsurancePeriodTextView.text = String.format("Insurance Period: 2")
        acceptNewRideReqButton.isEnabled = false
        pickupAPassengerButton.isEnabled = true
        cancelRequestButton.isEnabled = true
        dropAPassengerButton.isEnabled = false
        offDutyButton.isEnabled = false
    }

    private fun refreshUIForPeriod3() {
        currentStateTextView.text = String.format(
            """
                Passenger In Car:  TRUE
                Passenger Waiting For Pickup:  FALSE
                """.trimIndent()
        )
        currentInsurancePeriodTextView.text = String.format("Insurance Period: 3")
        acceptNewRideReqButton.isEnabled = false
        pickupAPassengerButton.isEnabled = false
        cancelRequestButton.isEnabled = false
        dropAPassengerButton.isEnabled = true
        offDutyButton.isEnabled = false
    }
}