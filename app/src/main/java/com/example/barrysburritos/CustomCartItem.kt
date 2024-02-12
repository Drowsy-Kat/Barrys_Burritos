package com.example.barrysburritos

data class CustomCartItem(
    val burritoName: String,
    val proteinChoice: String,
    val riceChoice: String,
    val beansChoice: String,
    val guacamole: Boolean,
    val sourCream: Boolean,
    val cheese: Boolean,
    val salsa: Boolean,
    val size: String,
    var quantity: Int,
    val price: Double
)