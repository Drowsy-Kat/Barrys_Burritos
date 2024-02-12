package com.example.barrysburritos

import android.content.Context
import com.google.gson.Gson
import java.io.IOException

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
