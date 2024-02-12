@file:Suppress("NAME_SHADOWING")

package com.example.barrysburritos

import Order
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

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


    @SuppressLint("SetTextI18n")
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

        var totalCost = 0.0
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
            val currentOrder = Order(premadeOrderItems, customItems)
            currentOrder.saveOrderToJson(requireContext())
            Log.d ("Cart", currentOrder.returnOrderFromJsonAsString(requireContext()))

            premadeCartViewModel.clearCart()
            customCartViewModel.clearCart()

            val allItems: MutableList<Any> = mutableListOf()
            allItems.addAll(customCartViewModel.cartItems.value ?: emptyList())
            allItems.addAll(premadeCartViewModel.cartItems.value ?: emptyList())
            adapter.updateItems(allItems)


        }

        view.findViewById<Button>(R.id.favoriteButton).setOnClickListener {

            if(!isFavoriteEmpty(requireContext())) {
                Toast.makeText(requireContext(), "No favorites to add to cart", Toast.LENGTH_SHORT).show()
            }else {
                val favoritesJson =
                    premadeCartViewModel.returnFavoritesFromJsonAsString(requireContext())
                val favorites = getFavorites(favoritesJson)
                //Toast.makeText(requireContext(), favorites.toString(), Toast.LENGTH_SHORT).show()

                if (favorites is PremadeOrderItem) {
                    val cartItem =
                        premadeCartViewModel.convertToCartItem(favorites, premadeViewModel)
                    premadeCartViewModel.addToCart(cartItem)
                    Toast.makeText(requireContext(), "Added to cart: ${cartItem.item.title}", Toast.LENGTH_SHORT).show()
                } else if (favorites is CustomCartItem) {
                    customCartViewModel.addToCart(favorites)
                    Toast.makeText(requireContext(), "Added to cart: ${favorites.burritoName}", Toast.LENGTH_SHORT).show()
                }
                val allItems = mutableListOf<Any>()
                allItems.addAll(customCartViewModel.cartItems.value ?: emptyList())
                allItems.addAll(premadeCartViewModel.cartItems.value ?: emptyList())
                adapter.updateItems(allItems)
            }
        }

        view.findViewById<Button>(R.id.viewLastOrderButton).setOnClickListener {
            val allItems = readOrderFromJson(requireContext())
            showLastOrderPopup(allItems)
        }

        view.findViewById<Button>(R.id.currentFavorite).setOnClickListener{
            if(!isFavoriteEmpty(requireContext())){
                Toast.makeText(requireContext(), "No favorites to show", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else {
                val favoritesJson =
                    premadeCartViewModel.returnFavoritesFromJsonAsString(requireContext())
                val favorites = getFavorites(favoritesJson)
                when (favorites) {
                    is PremadeOrderItem -> {
                        val premadeItem =
                            premadeCartViewModel.convertToCartItem(favorites, premadeViewModel)
                        val dialog = Dialog(requireContext())
                        dialog.setContentView(R.layout.current_favorite_premade_popup)
                        val title = dialog.findViewById<TextView>(R.id.favouritePopupTitlePremade)
                        val description = dialog.findViewById<TextView>(R.id.favouritePopupDescription)
                        val price = dialog.findViewById<TextView>(R.id.favouritePopupPricePremade)
                        val quantity = dialog.findViewById<TextView>(R.id.favouritePopupQuantityCustom)
                        val image = dialog.findViewById<ImageView>(R.id.favouritePopupImage)
                        title.text = premadeItem.item.title
                        description.text = premadeItem.item.description
                        price.text = "£${"%.2f".format(premadeItem.item.price)}"
                        quantity.text = " ${premadeItem.quantity}"
                        val resourceId =
                            getImageResourceByName(requireContext(), premadeItem.item.imageName)
                        image.setImageResource(resourceId)
                        dialog.findViewById<Button>(R.id.favouritePopupAddToCartPremade)
                            .setOnClickListener {
                                premadeCartViewModel.addToCart(premadeItem)

                                val allItems = mutableListOf<Any>()
                                allItems.addAll(customCartViewModel.cartItems.value ?: emptyList())
                                allItems.addAll(premadeCartViewModel.cartItems.value ?: emptyList())
                                adapter.updateItems(allItems)
                                dialog.dismiss()
                            }

                        dialog.show()


                    }

                    is CustomCartItem -> {
                        val dialog = Dialog(requireContext())
                        dialog.setContentView(R.layout.current_favorite_custom_popup)

                        val name = dialog.findViewById<TextView>(R.id.burritoNameTextView)
                        val rice = dialog.findViewById<TextView>(R.id.riceTextView)
                        val beans = dialog.findViewById<TextView>(R.id.beansTextView)
                        val protein = dialog.findViewById<TextView>(R.id.proteinTextView)
                        val salsa = dialog.findViewById<TextView>(R.id.salsaTextViewFavorite)
                        val guac = dialog.findViewById<TextView>(R.id.guacamoleTextViewFavorite)
                        val cheese = dialog.findViewById<TextView>(R.id.cheeseTextViewFavorite)
                        val sourCream = dialog.findViewById<TextView>(R.id.sourCreamTextViewFavorite)
                        val quantity = dialog.findViewById<TextView>(R.id.favouritePopupQuantityCustom)
                        val price = dialog.findViewById<TextView>(R.id.favouritePopupPriceCustom)


                        name.text = favorites.burritoName
                        rice.text = favorites.riceChoice
                        beans.text = favorites.beansChoice
                        protein.text = favorites.proteinChoice
                        guac.visibility = if (favorites.guacamole) View.VISIBLE else View.GONE
                        salsa.visibility = if (favorites.salsa) View.VISIBLE else View.GONE
                        cheese.visibility = if (favorites.cheese) View.VISIBLE else View.GONE
                        sourCream.visibility = if (favorites.sourCream) View.VISIBLE else View.GONE

                        quantity.text = " ${favorites.quantity}"
                        price.text = "£${"%.2f".format(favorites.price * favorites.quantity)}"

                        dialog.findViewById<Button>(R.id.favouritePopupAddToCartCustom)
                            .setOnClickListener {
                                customCartViewModel.addToCart(favorites)
                                val allItems = mutableListOf<Any>()
                                allItems.addAll(customCartViewModel.cartItems.value ?: emptyList())
                                allItems.addAll(premadeCartViewModel.cartItems.value ?: emptyList())
                                adapter.updateItems(allItems)
                                dialog.dismiss()
                            }
                        dialog.show()

                    }

                    else -> {
                        Toast.makeText(requireContext(), "No favorites to show", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }

        }


        return view
    }
    private fun getFavorites(json: String): Any? {
        val gson = Gson()
        return try {
            val jsonObject = JsonParser.parseString(json).asJsonObject
            val numElements = jsonObject.entrySet().size

            if (numElements == 2) {
                val premadeOrderItem = gson.fromJson(json, PremadeOrderItem::class.java)

                premadeOrderItem as Any // Cast to Any to match return type
            } else {

                val customCartItem = gson.fromJson(json, CustomCartItem::class.java)
                customCartItem as Any // Cast to Any to match return type
            }
        } catch (e: JsonParseException) {
            // Failed to deserialize JSON
            null
        }
    }

    private fun isFavoriteEmpty(context: Context): Boolean {
        val resourceId = R.raw.favorite
        val jsonString = try {
            context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception for debugging
            return true // Treat any exception (including file not found) as empty JSON
        }

        // Check if the JSON string is empty or not
        return jsonString.trim().isEmpty()
    }



    private fun returnOrderFromJsonAsString(context: Context): String {
        var orderJson = ""

        try {
            context.openFileInput("order.json").use { stream ->
                orderJson = stream.bufferedReader().use {
                    it.readText()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return orderJson
    }

    fun readOrderFromJson(context: Context): MutableList<Any> {
        val premadeItemsList: List<PremadeOrderItem>
        val customItemsList: List<CustomCartItem>

        val orderJson = returnOrderFromJsonAsString(context)

        if (orderJson.isNotEmpty()) {
            val gson = Gson()
            val orderType = object : TypeToken<Order>() {}.type
            val order = gson.fromJson<Order>(orderJson, orderType)

            premadeItemsList = order.premadeItems
            customItemsList = order.customItems
        } else {
            // If the JSON is empty, return an empty list
            Toast.makeText(context, "No order found", Toast.LENGTH_SHORT).show()
            return mutableListOf()
        }

        val allItems = mutableListOf<Any>()
        allItems.addAll(customItemsList)
        allItems.addAll(premadeItemsList.map { premadeCartViewModel.convertToCartItem(it,premadeViewModel) })

        return allItems
    }

    fun showLastOrderPopup(allItems: MutableList<Any>) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.last_order_popup)

        val recyclerView: RecyclerView = dialog.findViewById(R.id.previousOrderRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = LastOrderAdapter(allItems)
        recyclerView.adapter = adapter

        dialog.show()
    }


    @SuppressLint("DiscouragedApi")
    private fun getImageResourceByName(context: Context, imageName: String): Int {
        // Resolve image resource ID dynamically
        return context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )
    }




 }





