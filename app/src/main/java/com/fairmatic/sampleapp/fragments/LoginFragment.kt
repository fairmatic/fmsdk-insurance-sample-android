package com.fairmatic.sampleapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fairmatic.sampleapp.manager.SharedPrefsManager
import com.fairmatic.sampleapp.MainActivity
import com.fairmatic.sampleapp.R
import com.fairmatic.sampleapp.manager.FairmaticManager

class LoginFragment : Fragment(), View.OnClickListener {
    private var idEditText: EditText? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout: View = inflater.inflate(R.layout.fragment_login, container, false)
        idEditText = layout.findViewById(R.id.idEditText)
        layout.findViewById<View>(R.id.signupButton).setOnClickListener(this)
        return layout
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.signupButton -> signupButtonClicked()
        }
    }

    private fun signupButtonClicked() {
        val driverId: String = idEditText?.text.toString()
        Toast.makeText(context, "Signing up", Toast.LENGTH_SHORT).show()
        if (driverId != "") {
            // Save driver information
            context?.let { SharedPrefsManager.sharedInstance(it)}?.driverId = driverId
            // Initialize ZendriveSDK
            FairmaticManager.sharedInstance().initializeFairmaticSDK(context, driverId)
            // Load UI
            (activity as MainActivity).replaceFragment(OffDutyFragment())
        }
    }
}