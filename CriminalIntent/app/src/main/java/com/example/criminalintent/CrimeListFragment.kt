package com.example.criminalintent

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CrimeListFragment : Fragment(R.layout.fragment_crime_list) {

    private val model: CrimeListViewModel by viewModels()

    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var noCrimesGroup: Group
    private lateinit var newCrimeButton: Button
    private var adapter: CrimeAdapter? = null
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as? Callbacks
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setHasOptionsMenu(true)

        adapter = CrimeAdapter(requireContext(), callbacks)
        crimeRecyclerView.adapter = adapter

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