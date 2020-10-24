package com.example.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import java.util.*
import androidx.lifecycle.Observer
import java.text.SimpleDateFormat

private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val DIALOG_TIME = "DialogTime"
class Crimefragment:Fragment(),DatePickerFragment.Callbacks,TimePickerFragment.TimeCallbacks{
     lateinit var crime: Crime
    lateinit var textFild:TextView
    lateinit var dataButton:Button
    lateinit var timeButton:Button
    lateinit var slovedCheckBox:CheckBox
    private val crimeDetailViweModel:CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime= Crime()
        var id:UUID=arguments?.getSerializable("ARG_CRIME_ID")as UUID
       // Toast.makeText(context,id.toString(),Toast.LENGTH_LONG).show()
        crimeDetailViweModel.loadCrime(id)
    }
companion object{
    fun newInstance(crimId:UUID):Crimefragment{
        val arg=Bundle().apply {
            putSerializable("ARG_CRIME_ID",crimId)
        }
        return Crimefragment().apply {
            arguments=arg
        }
    }
}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // return super.onCreateView(inflater, container, savedInstanceState)
        val view=inflater.inflate(R.layout.fragment_crime,container,false)
        textFild=view.findViewById(R.id.crime_title)
        dataButton=view.findViewById(R.id.crime_date)
        slovedCheckBox=view.findViewById(R.id.crime_solved)
        timeButton=view.findViewById(R.id.crime_time)


     /*   dataButton.apply {
            text=crime.date.toString()
            crime.isSolved=false
        }*/


        slovedCheckBox.apply {
            setOnClickListener{
                crime.isSolved=isChecked
                jumpDrawablesToCurrentState()
            }
        }
        dataButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@Crimefragment,REQUEST_DATE)
               show(this@Crimefragment.requireFragmentManager(), DIALOG_DATE)
            }

        }
        timeButton.setOnClickListener {
           TimePickerFragment.newInstance2(crime.date).apply {
                setTargetFragment(this@Crimefragment,REQUEST_DATE)
                show(this@Crimefragment.requireFragmentManager(), DIALOG_TIME)
            }

        }
         return view




    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViweModel.crimeLiveData.observe(viewLifecycleOwner, Observer { crime-> crime?.let {
                crime->this.crime=crime
            updateUI()

        } })

    }
    fun updateUI(){
        textFild.setText(crime.title)
        dataButton.setText(crime.date.toString())
        slovedCheckBox.isChecked=crime.isSolved
        timeButton.text = SimpleDateFormat("HH:MM").format(crime.date.time)
    }
    override fun onStart() {
        super.onStart()
        val titleWatcher=object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    crime.title=s.toString()
            }

        }
       textFild.addTextChangedListener(titleWatcher)
    }
    override fun onStop() {
        super.onStop()
       crimeDetailViweModel.saveCrime(crime)
    }

    override fun onDateSelected(date: Date) {
    crime.date=date
        updateUI()
    }

    override fun onTimeSelected(date: Date) {
        crime.date=date
        updateUI()
    }
}

