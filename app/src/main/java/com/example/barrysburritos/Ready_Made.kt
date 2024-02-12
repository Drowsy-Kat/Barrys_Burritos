package com.example.barrysburritos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView



class Ready_Made : Fragment() {
    private lateinit var premadeCartViewModel: PremadeCartViewModel
    private lateinit var premadeViewModel: PremadeViewModel
    private lateinit var adapter: PremadeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize premadeCartViewModel using ViewModelProvider from the activity's scope
        premadeCartViewModel = ViewModelProvider(requireActivity())[PremadeCartViewModel::class.java]
        // Initialize premadeViewModel using ViewModelProvider from the activity's scope
        premadeViewModel = ViewModelProvider(requireActivity())[PremadeViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_ready_made, container, false)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.ready_made_recycler_view)



        // Get premade items from premadeViewModel
        val premadeList = premadeViewModel.premadeItems
        Log.d("Ready_Made", "Premade List: $premadeList")

        // Initialize adapter
        adapter = PremadeAdapter(premadeList, premadeCartViewModel)
        recyclerView.adapter = adapter

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the observer to avoid memory leaks
        premadeCartViewModel.cartItems.removeObservers(viewLifecycleOwner)
    }
}

