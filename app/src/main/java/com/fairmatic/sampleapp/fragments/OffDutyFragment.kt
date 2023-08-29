package com.fairmatic.sampleapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fairmatic.sampleapp.manager.TripManager
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sampleapp.MainActivity
import com.fairmatic.sampleapp.R
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult

class OffDutyFragment : Fragment(), View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout: View = inflater.inflate(R.layout.fragment_offduty, container, false)
        layout.findViewById<View>(R.id.goOnDutyButton).setOnClickListener(this)
        return layout
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.goOnDutyButton -> goOnDutyButtonClicked()
        }
    }


    private fun goOnDutyButtonClicked() {
        Log.d(Constants.LOG_TAG_DEBUG, "goOnDutyButtonClicked")
        Toast.makeText(context, "Going on duty", Toast.LENGTH_SHORT).show()
        context?.let { TripManager.sharedInstance(it)?.goOnDuty(requireContext(), object : FairmaticOperationCallback{
            override fun onCompletion(result: FairmaticOperationResult) {
                if(result is FairmaticOperationResult.Success){
                    (activity as MainActivity).replaceFragment(OnDutyFragment())
                }
                else{
                    Log.d(Constants.LOG_TAG_DEBUG, "goOnDutyButtonClicked failed")
                }
            }
        }) }
    }
}