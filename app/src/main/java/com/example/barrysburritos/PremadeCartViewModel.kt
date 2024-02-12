package com.example.barrysburritos

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import java.io.IOException


class PremadeCartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<PremadeCartItem>>()
    val cartItems: LiveData<List<PremadeCartItem>> get() = _cartItems

    private val _totalCost = MutableLiveData<Double>()
    val totalCost: LiveData<Double> get() = _totalCost



    private fun updateTotalCost() {
        var totalCost = 0.0
        for (item in _cartItems.value.orEmpty()) {
            totalCost += item.item.price * item.quantity
        }
        _totalCost.value = totalCost
    }
    fun increaseQuantity(item: PremadeCartItem) {
        item.quantity++
        updateTotalCost()
    }

    // Function to decrease quantity
    fun decreaseQuantity(item: PremadeCartItem) {
        if (item.quantity > 1) {
            item.quantity--
            updateTotalCost()
        }
    }

    fun addToCart(cartItem: PremadeCartItem) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        _cartItems.value = currentList + cartItem
        updateTotalCost()
    }

    fun removeFromCart(cartItem: PremadeCartItem) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        currentList.remove(cartItem)
        _cartItems.value = currentList
        updateTotalCost()
    }
    fun clearCart() {
        _cartItems.value = emptyList()
        _totalCost.value = 0.0
    }


    fun addToFavorites(premadeCartItem: PremadeCartItem, context: Context) {
        val gson = Gson()
        val resourceId = R.raw.favorite // Assuming the resource ID of favorite.json is 'favorite'
        val inputStream = context.resources.openRawResource(resourceId)

        // Read existing favorites or create an empty list if there's an error
        val existingFavorites = try {
            inputStream.bufferedReader().use { reader ->
                val json = reader.readText()
                if (json.isNotEmpty()) {
                    gson.fromJson(json, PremadeOrderItem::class.java)
                } else {
                    null
                }
            }
        } catch (e: IOException) {
            null
        } finally {
            inputStream.close()
        }

        // Convert premadeCartItem to PremadeOrderItem
        val premadeOrderItem = convertToOrderItem(premadeCartItem)

        // Write the new item to favorites
        context.openFileOutput("favorite.json", Context.MODE_PRIVATE).use { outputStream ->
            outputStream.bufferedWriter().use { writer ->
                writer.write(gson.toJson(premadeOrderItem))
            }
        }
    }
    private fun convertToOrderItem(premadeCartItem: PremadeCartItem): PremadeOrderItem {
        val item: Int = premadeCartItem.item.id
        val quantity: Int = premadeCartItem.quantity
        return PremadeOrderItem(item, quantity)
    }

    fun convertToCartItem(premadeOrderItem: PremadeOrderItem, premadeViewModel: PremadeViewModel): PremadeCartItem {

        val item = premadeViewModel.premadeItems.find { it.id == premadeOrderItem.itemId } ?: throw IllegalArgumentException("Premade item not found")
        val quantity = premadeOrderItem.quantity
        return PremadeCartItem(item, quantity)
    }

    fun returnFavoritesFromJsonAsString(context: Context): String {
        var orderJson = ""
        try {
            context.openFileInput("favorite.json").use { stream ->
                orderJson = stream.bufferedReader().use {
                    it.readText()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return orderJson
    }

}