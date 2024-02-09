package com.example.barrysburritos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



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

}