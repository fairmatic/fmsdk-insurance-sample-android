package com.fairmatic.sampleapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fairmatic.sampleapp.Constants
import com.fairmatic.sampleapp.manager.SharedPrefsManager
import com.fairmatic.sampleapp.MainActivity
import com.fairmatic.sampleapp.R
import com.fairmatic.sampleapp.manager.FairmaticManager
import com.fairmatic.sdk.Fairmatic
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult

// The driver is currently off duty
class LoginFragment(val goOffDuty: () -> Unit) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout: View = inflater.inflate(R.layout.fragment_login, container, false)
        layout.findViewById<View>(R.id.signupButton).setOnClickListener { signupButtonClicked() }

        return layout
    }

    private fun signupButtonClicked() {
        val driverId: String = view?.findViewById<EditText>(R.id.idEditText)?.text.toString()

        // Use the Fairmatic SDK to validate the driver id
        if (driverId.isBlank() && !Fairmatic.isValidInputParameter(driverId)) {
            Toast.makeText(context, "Please enter a valid driver id", Toast.LENGTH_SHORT).show()
            return
        }
        if (context == null) {
            Toast.makeText(context, "LoginFragment not attached to a host", Toast.LENGTH_SHORT)
                .show()
            return
        }

        FairmaticManager
            .initializeFairmaticSDK(
                requireContext(),
                driverId,
                object : FairmaticOperationCallback {
                    override fun onCompletion(result: FairmaticOperationResult) {
                        if (result is FairmaticOperationResult.Success) {
                            Log.d(Constants.LOG_TAG_DEBUG, "Successfully initialized Fairmatic SDK")
                            Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                            SharedPrefsManager.sharedInstance(requireContext()).driverId = driverId
                            goOffDuty()
                        } else if (result is FairmaticOperationResult.Failure) {
                            Log.d(
                                Constants.LOG_TAG_DEBUG,
                                "Failed to initialize Fairmatic SDK : ${result.error} : ${result.errorMessage}"
                            )
                            Toast.makeText(
                                context,
                                "Sign up failed: ${result.errorMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
    }
}
