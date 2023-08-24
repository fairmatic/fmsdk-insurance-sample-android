package com.fairmatic.sampleapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.fairmatic.sampleapp.fragments.OffDutyFragment
import com.fairmatic.sampleapp.fragments.OnDutyFragment
import com.fairmatic.sampleapp.fragments.LoginFragment
import com.fairmatic.sampleapp.manager.SharedPrefsManager
import com.fairmatic.sampleapp.manager.TripManager
import com.fairmatic.sampleapp.manager.FairmaticManager
import com.fairmatic.sdk.classes.FairmaticOperationCallback
import com.fairmatic.sdk.classes.FairmaticOperationResult

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFirstFragment()
    }

    override fun onResume() {
        super.onResume()
        val driverId: String? = SharedPrefsManager.sharedInstance(this)?.driverId
        // if the user is already logged in or setup previously failed, the Fairmatic SDK should be set up.
        if (driverId != null ||
            SharedPrefsManager.sharedInstance(this)?.shouldRetryFairmaticSetup() == true
        ) {
            FairmaticManager.sharedInstance().initializeFairmaticSDK(this, driverId, object: FairmaticOperationCallback {
                override fun onCompletion(result: FairmaticOperationResult) {
                    if(result is FairmaticOperationResult.Failure){
                        val errorMessage = result.error
                        Toast.makeText(this@MainActivity, "Failed to initialize Fairmatic SDK : ${errorMessage.name}", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@MainActivity, "Successfully initialized Fairmatic SDK", Toast.LENGTH_SHORT).show()
                        loadFirstFragment()
                    }
                }
            })
        }
        // check Fairmatic settings on app resume if there are errors/warnings present
        FairmaticManager.sharedInstance().maybeCheckFairmaticSettings(this)
    }

    private fun loadFirstFragment() {
        val firstFragment: Fragment = if (SharedPrefsManager.sharedInstance(this)?.driverId != null) {
            if (TripManager.sharedInstance(this)?.tripManagerState?.isUserOnDuty == true) {
                OnDutyFragment()
            } else {
                OffDutyFragment()
            }
        } else {
            LoginFragment()
        }
        replaceFragment(firstFragment)
    }

    fun replaceFragment(newFragment: Fragment) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.mainContentView, newFragment)
        ft.commit()
    }
}