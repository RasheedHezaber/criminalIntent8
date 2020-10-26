package com.example.criminalintent

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.*
import javax.security.auth.callback.Callback


class CrimeListFragment : Fragment() {
    private val crimeListViewModel: CrimeListViweModle by lazy {
        ViewModelProviders.of(this).get(CrimeListViweModle::class.java)
    }
    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var addCrimeButton: Button
    private lateinit var addCrimeText: TextView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }
    var calllBacks:CallBacks?=null
    interface CallBacks{
        fun onItemSelected(crimeId:UUID)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        calllBacks=context as CallBacks
    }

    override fun onDetach() {
        super.onDetach()
        calllBacks=null
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        //updateUI()
        crimeRecyclerView.adapter = adapter
        addCrimeText = view.findViewById(R.id.list_empty)
        addCrimeButton=view.findViewById(R.id.addCrimeButton)
        return view
    }

     fun updateUI(crimes: List<Crime>) {
        //val crimes = crimeListViewModel.crimes
   if (crimes.isNotEmpty()){
       adapter = CrimeAdapter(crimes)

       addCrimeText.visibility = View.GONE
       addCrimeButton.visibility = View.GONE

       crimeRecyclerView.adapter = adapter
       val adapter = crimeRecyclerView.adapter as CrimeAdapter
       adapter.submitList(crimes)
   }else {

       crimeRecyclerView.visibility = View.GONE

   }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      crimeListViewModel.crimeListLiveData.observe(viewLifecycleOwner, Observer
      { crimes-> crimes.let { updateUI(crimes) }})

        addCrimeButton.setOnClickListener {
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
           calllBacks?.onItemSelected(crime.id)

        }
    }
    abstract open  class CrimeHolder(view: View) : RecyclerView.ViewHolder(view){
        abstract open fun bind(item:Crime)

    }
    private inner class CrimeViweHolder(view: View) :  CrimeHolder(view), View.OnClickListener {
        private lateinit var crime: Crime
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)


        init { itemView.setOnClickListener(this) }



        override fun onClick(v: View) {
            // Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
            /* val fragment = Crimefragment()
            val fm=activity?.supportFragmentManager
             fm?.beginTransaction()?.replace(R.id.fragment_container, fragment)?.commit()*/
            calllBacks?.onItemSelected(crime.id)
        }
        override fun bind(crime: Crime) {

            this.crime = crime
           titleTextView.setText(crime.title)
           dateTextView.setText(crime.date.toString())
            solvedImageView.visibility=if(crime.isSolved){
                View.VISIBLE
            }
            else
                View.GONE
        }
    }


    private  inner class SecondCrimeHolder(view: View) : RecyclerView.ViewHolder(view) ,
        View.OnClickListener {
        private lateinit var crime: Crime
        val requiredTextView: TextView = itemView.findViewById(R.id.rcrime_title)
        val requireddateTextView: TextView = itemView.findViewById(R.id.rcrime_date)

        init {
            itemView.setOnClickListener(this)
        }
        fun bind(crime: Crime) {
            this.crime = crime

            requiredTextView.setText(crime.title)
            requireddateTextView.text= DateFormat.getDateInstance(DateFormat.FULL).format(this.crime.date).toString()
        }

        override fun onClick(v: View?) {

            calllBacks?.onItemSelected(crime.id)
        }

    }
    private inner class CrimeAdapter(var crimes: List<Crime>) :androidx.recyclerview.widget.ListAdapter<Crime , RecyclerView.ViewHolder>(CrimeDiffUtil())  {
        val requiredCrime = 1
        val normalCrime = 2


        override fun getItemViewType(position: Int): Int {
            return if (crimes[position].isSolved )
                return requiredCrime
            else
                return normalCrime

        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view: View?
            var recyclerViewholder: RecyclerView.ViewHolder
            when (viewType) {
                requiredCrime -> { val view = layoutInflater.inflate(R.layout.list_item_police, parent, false)

                    recyclerViewholder = SecondCrimeHolder(view)
                }
                else -> {
                    val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                    recyclerViewholder = CrimeViweHolder(view)
                }

            }
            return recyclerViewholder

        }

        override fun getItemCount(): Int {
            return crimes.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val crime = crimes[position]
            if (holder is SecondCrimeHolder)
                holder.bind(crime)
            else
                if(holder is CrimeViweHolder)
                    holder.bind(crime)

        }
    }
    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
     class CrimeDiffUtil : DiffUtil.ItemCallback<Crime>(){
        override fun areItemsTheSame(oldItem: Crime , newItem: Crime): Boolean {

            return oldItem.id === newItem.id
    }

         override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
         return    (oldItem.id == newItem.id &&
                     oldItem.title == newItem.title &&
                     oldItem.date == newItem.date &&
                     oldItem.isSolved == newItem.isSolved)
         }
     }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list,menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                calllBacks?.onItemSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)

    }
}
