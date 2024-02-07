package com.example.barrysburritos
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel




class CustomCartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<CustomCartItem>>()
    val cartItems: LiveData<List<CustomCartItem>> get()  = _cartItems



    fun addToCart(cartItem: CustomCartItem) {
        val currentList = _cartItems.value ?: listOf()
        _cartItems.value = currentList + cartItem
    }


    fun removeFromCart(cartItem: CustomCartItem) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        currentList.remove(cartItem)
        _cartItems.value = currentList
    }

    fun updateCartItem(updatedCartItem: CustomCartItem) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it == updatedCartItem }
        if (index != -1) {
            currentList[index] = updatedCartItem
            _cartItems.value = currentList
        }

    }


}


