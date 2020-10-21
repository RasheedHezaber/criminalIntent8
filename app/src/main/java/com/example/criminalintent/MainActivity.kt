package com.example.criminalintent

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() ,CrimeListFragment.CallBacks{

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment == null) {
                val fragment = CrimeListFragment.newInstance()
                supportFragmentManager . beginTransaction ().add(
                    R.id.fragment_container,
                    fragment
                ).commit()
            }
        }catch (e:Exception){
            print(e.message)
        }
    }

    override fun onItemSelected(crimeId: UUID) {
        val fragment = Crimefragment.newInstance(crimeId)
        val fm=supportFragmentManager
        fm?.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }
}
