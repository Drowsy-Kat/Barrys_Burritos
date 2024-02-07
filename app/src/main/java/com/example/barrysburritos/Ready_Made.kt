package com.example.barrysburritos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView



class Ready_Made : Fragment() {
    private lateinit var premadeCartViewModel: PremadeCartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        premadeCartViewModel = ViewModelProvider(requireActivity())[PremadeCartViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       val rootView = inflater.inflate(R.layout.fragment_ready_made, container, false)
       val recyclerView: RecyclerView = rootView.findViewById(R.id.ready_made_recycler_view)


        premadeCartViewModel.cartItems.observe(viewLifecycleOwner) { cartItemsList ->
            val cartSize = cartItemsList.size.toString()
            Toast.makeText(requireContext(), cartSize, Toast.LENGTH_SHORT).show()
        }


       val premadeList = PremadeAdapter.loadFromJson(requireContext(), "premade.json")

        val adapter = PremadeAdapter(premadeList,premadeCartViewModel, this)

       recyclerView.adapter = adapter



       recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

       return rootView



    }


}