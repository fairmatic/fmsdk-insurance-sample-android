package com.fairmatic.sampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.fairmatic.sampleapp.fragments.OffDutyFragment
import com.fairmatic.sampleapp.fragments.OnDutyFragment
import com.fairmatic.sampleapp.fragments.LoginFragment
import com.fairmatic.sampleapp.manager.SharedPrefsManager
import com.fairmatic.sampleapp.manager.FairmaticManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFirstFragment()
    }

    override fun onResume() {
        super.onResume()
        FairmaticManager.checkFairmaticSettings(this)
    }

    private fun loadFirstFragment() {
        val firstFragment: Fragment =
            if (!SharedPrefsManager.sharedInstance(this).driverId.isNullOrBlank()) {
                if (SharedPrefsManager.sharedInstance(this).isUserOnDuty) {
                    getOnDutyFragment()
                } else {
                    getOffDutyFragment()
                }
            } else {
                getLoginFragment()
            }
        replaceFragment(firstFragment)
    }

    private fun goOffDuty() {
        replaceFragment(getOffDutyFragment())
    }

    // The driver is currently off duty
    private fun getOffDutyFragment(): OffDutyFragment {
        return OffDutyFragment( goOnDuty = { goOnDuty() })
    }

    private fun goOnDuty() {
        replaceFragment(getOnDutyFragment())
    }

    // The driver is currently on duty
    private fun getOnDutyFragment(): OnDutyFragment {
        return OnDutyFragment (goOffDuty = {goOffDuty() })
    }

    // The driver is yet to login
    private fun getLoginFragment(): LoginFragment {
        return LoginFragment( goOffDuty = { goOffDuty() })
    }

    // Replace the current fragment with a new fragment
    private fun replaceFragment(newFragment: Fragment) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.mainContentView, newFragment)
        ft.commit()
    }
}