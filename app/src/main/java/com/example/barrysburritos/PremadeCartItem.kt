package com.example.barrysburritos

data class PremadeCartItem(val item: PremadeItem,var quantity: Int)
{

    fun increaseQuantity() {
        quantity++
    }

    fun decreaseQuantity() {
        if (quantity > 0) {
            quantity--
        }
    }
}
