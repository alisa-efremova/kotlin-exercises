package com.example.criminalintent

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CrimeListFragment : Fragment() {

    private val model: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var noCrimesGroup: Group
    private lateinit var newCrimeButton: Button
    private var adapter: CrimeAdapter? = null
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as? Callbacks
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        initViews(view)
        setHasOptionsMenu(true)

        adapter = CrimeAdapter(callbacks)
        crimeRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newCrimeButton.setOnClickListener {
            onAddNewCrime()
        }

        updateUI()

        model.crimeListLiveData.observe(
                viewLifecycleOwner,
                { crimes ->
                    crimes?.let {
                        adapter?.submitList(crimes)
                        updateUI()
                    }
                }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                onAddNewCrime()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun onAddNewCrime() {
        val crime = Crime()
        model.addCrime(crime)
        callbacks?.onCrimeSelected(crime.id)
    }

    private fun updateUI() {
        if (model.hasCrimes()) {
            crimeRecyclerView.visibility = View.VISIBLE
            noCrimesGroup.visibility = View.GONE
        } else {
            crimeRecyclerView.visibility = View.GONE
            noCrimesGroup.visibility = View.VISIBLE
        }
    }

    private fun initViews(view: View) {
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        noCrimesGroup = view.findViewById(R.id.no_crimes_group)
        newCrimeButton = view.findViewById(R.id.new_crime_button)
    }
}