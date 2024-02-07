package com.example.barrysburritos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class PremadeCartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<PremadeCartItem>>()
    val cartItems: LiveData<List<PremadeCartItem>> get() = _cartItems



    fun addToCart(cartItem: PremadeCartItem) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        _cartItems.value = currentList + cartItem
    }

    fun removeFromCart(cartItem: PremadeCartItem) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        currentList.remove(cartItem)
        _cartItems.value = currentList
    }

}