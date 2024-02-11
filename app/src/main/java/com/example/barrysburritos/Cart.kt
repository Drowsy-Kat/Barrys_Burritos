package com.example.barrysburritos

import Order
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class Cart : Fragment() {
    private lateinit var customCartViewModel: CustomCartViewModel
    private lateinit var premadeCartViewModel: PremadeCartViewModel
    private lateinit var totalCostObserver: Observer<Double>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.cartRecyclerView)

        customCartViewModel = ViewModelProvider(requireActivity())[CustomCartViewModel::class.java]
        premadeCartViewModel = ViewModelProvider(requireActivity())[PremadeCartViewModel::class.java]
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Combine the items from both view models
        val allItems = mutableListOf<Any>()
        allItems.addAll(customCartViewModel.cartItems.value ?: emptyList())
        allItems.addAll(premadeCartViewModel.cartItems.value ?: emptyList())

        // Set up the adapter
        val adapter = CartAdapter(allItems , customCartViewModel, premadeCartViewModel)
        recyclerView.adapter = adapter

        var totalCost: Double = 0.0
        for (item in allItems) {
            if (item is CustomCartItem) {
                totalCost += item.price * item.quantity
            } else if (item is PremadeCartItem) {
                totalCost += item.item.price * item.quantity
            }
        }
        totalCostObserver = Observer { totalCost ->
            val cartPriceTextView = view.findViewById<TextView>(R.id.cartPriceTextView)
            cartPriceTextView.text = "Total: £${"%.2f".format(totalCost)}"
        }

        customCartViewModel.totalCost.observe(viewLifecycleOwner, totalCostObserver)
        premadeCartViewModel.totalCost.observe(viewLifecycleOwner, totalCostObserver)

        val checkoutButton = view.findViewById<Button>(R.id.checkoutButton)
        checkoutButton.setOnClickListener {
            val customItems = customCartViewModel.cartItems.value ?: emptyList()
            val premadeOrderItems = premadeCartViewModel.cartItems.value?.map { cartItem ->
                PremadeOrderItem(
                    itemId = cartItem.item.id,
                    quantity = cartItem.quantity
                )
            } ?: emptyList()
            val currentOrder = Order(customItems, premadeOrderItems)
            currentOrder.saveOrderToJson(requireContext())
            Log.d ("Cart", currentOrder.returnOrderFromJsonAsString(requireContext()))

            premadeCartViewModel.clearCart()
            customCartViewModel.clearCart()

            val allItems = mutableListOf<Any>()
            allItems.addAll(customCartViewModel.cartItems.value ?: emptyList())
            allItems.addAll(premadeCartViewModel.cartItems.value ?: emptyList())
            adapter.updateItems(allItems)
            




        }






        return view
    }
 }

// TODO: add to favorites feature
// TODO: add to cart from favorites feature


