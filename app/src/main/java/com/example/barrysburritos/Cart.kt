package com.example.barrysburritos

import Order
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.io.IOException


class Cart : Fragment() {
    private lateinit var customCartViewModel: CustomCartViewModel
    private lateinit var premadeCartViewModel: PremadeCartViewModel
    private lateinit var premadeViewModel: PremadeViewModel
    private lateinit var totalCostObserver: Observer<Double>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.cartRecyclerView)

        customCartViewModel = ViewModelProvider(requireActivity())[CustomCartViewModel::class.java]
        premadeCartViewModel = ViewModelProvider(requireActivity())[PremadeCartViewModel::class.java]
        premadeViewModel = ViewModelProvider(requireActivity())[PremadeViewModel::class.java]
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

        view.findViewById<Button>(R.id.favoriteButton).setOnClickListener {
            Toast.makeText(requireContext(), "Adding to favorites", Toast.LENGTH_SHORT).show()

            val favoritesJson = premadeCartViewModel.returnFavoritesFromJsonAsString(requireContext())
            val favorites = getFavorites(favoritesJson)
            //Toast.makeText(requireContext(), favorites.toString(), Toast.LENGTH_SHORT).show()

            if (favorites is PremadeOrderItem) {
                val cartItem = premadeCartViewModel.convertToCartItem(favorites, premadeViewModel)
                premadeCartViewModel.addToCart(cartItem)
            } else if (favorites is CustomCartItem) {
                customCartViewModel.addToCart(favorites)
            } else {
                Toast.makeText(requireContext(), "Failed to add to favorites", Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(), favoritesJson, Toast.LENGTH_SHORT).show()
            }
            val allItems = mutableListOf<Any>()
            allItems.addAll(customCartViewModel.cartItems.value ?: emptyList())
            allItems.addAll(premadeCartViewModel.cartItems.value ?: emptyList())
            adapter.updateItems(allItems)
        }



        return view
    }
    fun getFavorites(json: String): Any? {
        val gson = Gson()
        return try {
            val jsonObject = JsonParser.parseString(json).asJsonObject
            val numElements = jsonObject.entrySet().size

            if (numElements == 2) {
                val premadeOrderItem = gson.fromJson(json, PremadeOrderItem::class.java)
                Toast.makeText(requireContext(), "2", Toast.LENGTH_SHORT).show()
                premadeOrderItem as Any // Cast to Any to match return type
            } else {
                Toast.makeText(requireContext(), "$numElements", Toast.LENGTH_SHORT).show()
                val customCartItem = gson.fromJson(json, CustomCartItem::class.java)
                customCartItem as Any // Cast to Any to match return type
            }
        } catch (e: JsonParseException) {
            // Failed to deserialize JSON
            null
        }
    }














 }





