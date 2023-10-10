package com.fairmatic.sampleapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sampleapp.R
import com.fairmatic.sampleapp.manager.FairmaticManager
import com.fairmatic.sampleapp.manager.SharedPrefsManager
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult

class OffDutyFragment(val goOnDuty: () -> Unit) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout: View = inflater.inflate(R.layout.fragment_offduty, container, false)
        layout.findViewById<View>(R.id.goOnDutyButton)
            .setOnClickListener { goOnDutyButtonClicked() }
        return layout
    }


    private fun goOnDutyButtonClicked() {
        Log.d(Constants.LOG_TAG_DEBUG, "Go On Duty button clicked")
        Toast.makeText(context, "Going on duty", Toast.LENGTH_SHORT).show()

        if (context == null) {
            Toast.makeText(context, "OffDutyFragment not attached to a host", Toast.LENGTH_SHORT)
                .show()
            return
        }

        FairmaticManager.startInsurancePeriod1(
            requireContext(),
            object : FairmaticOperationCallback {
                override fun onCompletion(result: FairmaticOperationResult) {
                    if (result is FairmaticOperationResult.Success) {
                        Log.d(Constants.LOG_TAG_DEBUG, "Start period 1 successful")
                    } else {
                        Log.d(Constants.LOG_TAG_DEBUG, "Start period 1 failed")
                    }
                }
            })

        SharedPrefsManager.sharedInstance(requireContext()).isUserOnDuty = true
        goOnDuty()
    }
}