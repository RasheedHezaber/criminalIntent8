package com.example.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import java.util.*
import androidx.lifecycle.Observer

import java.text.SimpleDateFormat
private const val REQUEST_CONTACT = 1
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val DIALOG_TIME = "DialogTime"
private const val DATE_FORMAT = "EEE, MMM, dd"
class Crimefragment:Fragment(),DatePickerFragment.Callbacks,TimePickerFragment.TimeCallbacks{
     lateinit var crime: Crime
    lateinit var textFild:TextView
    lateinit var dataButton:Button
    lateinit var timeButton:Button
    lateinit var slovedCheckBox:CheckBox
    lateinit var suspectButton:Button
    private lateinit var reportButton: Button
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
        reportButton = view.findViewById(R.id.crime_report) as Button

        reportButton.setOnClickListener {
           Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimereport())
                putExtra(Intent.EXTRA_TITLE, crime.title)
           }.also { intent ->
               val chooserIntent =
                   Intent.createChooser(intent, getString(R.string.send_report))
               startActivity(chooserIntent)
            }}
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent,
                    PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val cursor = requireActivity().contentResolver
                    .query(contactUri!!, queryFields, null, null, null)
                cursor?.use {

                    if (it.count == 0) {
                        return
                    }
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    crimeDetailViweModel.saveCrime(crime)
                    suspectButton.text = suspect
            }
        }
    }}
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
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
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
fun getCrimereport():String{

    val solvedString = if (crime.isSolved) {
        getString(R.string.crime_report_solved)
    } else {
        getString(R.string.crime_report_unsolved)
    }
    val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
    var suspect = if (crime.suspect.isBlank()) {
        getString(R.string.crime_report_no_suspect)
    } else {
        getString(R.string.crime_report_suspect, crime.suspect)
    }
    return getString(R.string.crime_report,
        crime.title, dateString, solvedString, suspect)
}
    override fun onDateSelected(date: Date) {
    crime.date=date
        updateUI()
    }

    override fun onTimeSelected(date: Date) {
        crime.date=date
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list,menu)
    }
}

