package com.example.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import java.util.*
private const val ARG_TIME = "time"
class TimePickerFragment:DialogFragment() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cal = Calendar.getInstance()
        val timeListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val resultTime = cal.time

            targetFragment.let {

                    fragment ->
                (fragment as TimeCallbacks).onTimeSelected(resultTime)
            }

        }

        val time = arguments?.getSerializable(ARG_TIME) as Date
        cal.time = time

        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val Minute = cal.get(Calendar.MINUTE)


        return TimePickerDialog(requireContext() , timeListener , hour , Minute ,
            false

        )
    }
    companion object {
        fun newInstance2(time: Date): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TIME , time)
            }

            return TimePickerFragment().apply {
                arguments = args
            }

        }
    }
    interface TimeCallbacks {
        fun onTimeSelected(date: Date)
    }
}