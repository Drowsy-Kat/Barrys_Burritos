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
){
    override fun toString(): String {
        return """
            Item Name: $burritoName
            Protein Choice: $proteinChoice
            Rice Choice: $riceChoice
            Beans Choice: $beansChoice
            Guacamole: $guacamole
            Sour Cream: $sourCream
            Cheese: $cheese
            Salsa: $salsa
            Size: $size
            Quantity: $quantity
        """.trimIndent()
    }

    fun increaseQuantity() {
        quantity++
    }

    fun decreaseQuantity() {
        if (quantity > 0) {
            quantity--
        }
    }

}