package com.example.barrysburritos
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel




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

    fun updateCartItem(updatedCartItem: CustomCartItem) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it == updatedCartItem }
        if (index != -1) {
            currentList[index] = updatedCartItem
            _cartItems.value = currentList
        }

    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _totalCost.value = 0.0
    }


}


