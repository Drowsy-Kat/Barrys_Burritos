package com.example.barrysburritos
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.io.IOException


class CustomCartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<CustomCartItem>>()
    val cartItems: LiveData<List<CustomCartItem>> get()  = _cartItems

    private val _totalCost = MutableLiveData<Double>()
    val totalCost: LiveData<Double> get() = _totalCost

    private fun updateTotalCost() {
        var totalCost = 0.0
        for (item in _cartItems.value.orEmpty()) {
            totalCost += item.price * item.quantity
        }
        _totalCost.value = totalCost
    }
    fun increaseQuantity(item: CustomCartItem) {
        item.quantity++
        updateTotalCost()
    }

    // Function to decrease quantity
    fun decreaseQuantity(item: CustomCartItem) {
        if (item.quantity > 1) {
            item.quantity--
            updateTotalCost()
        }
    }


    fun addToCart(cartItem: CustomCartItem) {
        val currentList = _cartItems.value ?: listOf()
        _cartItems.value = currentList + cartItem
        updateTotalCost()
    }


    fun removeFromCart(cartItem: CustomCartItem) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        currentList.remove(cartItem)
        _cartItems.value = currentList
        updateTotalCost()
    }



    fun clearCart() {
        _cartItems.value = emptyList()
        _totalCost.value = 0.0
    }


    fun addToFavorites(cartItem: CustomCartItem, context: Context) {
        val gson = Gson()
        val resourceId = R.raw.favorite // Assuming the resource ID of favorite.json is 'favorite'
        val inputStream = context.resources.openRawResource(resourceId)

        // Read existing favorites or create an empty list if there's an error
        try {
            inputStream.bufferedReader().use { reader ->
                val json = reader.readText()
                if (json.isNotEmpty()) {
                    gson.fromJson(json, CustomCartItem::class.java)
                } else {
                    null
                }
            }
        } catch (e: IOException) {
            null
        } finally {
            inputStream.close()
        }

        // Add the new item to favorites



        // Write the updated favorites list back to the file
        context.openFileOutput("favorite.json", Context.MODE_PRIVATE).use { outputStream ->
            outputStream.bufferedWriter().use { writer ->
                writer.write(gson.toJson(cartItem))
            }
        }
    }


}


