package com.example.barrysburritos



import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LastOrderAdapter(
    private var items: MutableList<Any>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val CUSTOM_ITEM = 0
    private val PREMADE_ITEM = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CUSTOM_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.last_order_custom, parent, false)
                CustomLastOrderViewHolder(view)
            }
            PREMADE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.last_order_premade, parent, false)
                PremadeLastOrderViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is CustomLastOrderViewHolder -> {
                val customCartItem = item as CustomCartItem

                holder.bind(customCartItem)






            }

            is PremadeLastOrderViewHolder
            -> {
                val premadeCartItem = item as PremadeCartItem







                holder.bind(premadeCartItem)
            }
        }

    }



    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CustomCartItem -> CUSTOM_ITEM
            is PremadeCartItem -> PREMADE_ITEM
            else -> throw IllegalArgumentException("Invalid data type")
        }
    }
}

// ViewHolder for CustomCartItem
class CustomLastOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @SuppressLint("SetTextI18n")
    fun bind(customCartItem: CustomCartItem) {
        // Bind data to views
        itemView.findViewById<TextView>(R.id.burritoNameTextView).text = customCartItem.burritoName
        itemView.findViewById<TextView>(R.id.proteinTextView).text = customCartItem.proteinChoice
        itemView.findViewById<TextView>(R.id.riceTextView).text = customCartItem.riceChoice
        itemView.findViewById<TextView>(R.id.beansTextView).text = customCartItem.beansChoice
        itemView.findViewById<TextView>(R.id.guacamoleTextView).visibility = if (customCartItem.guacamole) View.VISIBLE else View.GONE
        itemView.findViewById<TextView>(R.id.sourCreamTextView).visibility = if (customCartItem.sourCream) View.VISIBLE else View.GONE
        itemView.findViewById<TextView>(R.id.salsaTextViewCart).visibility = if (customCartItem.salsa) View.VISIBLE else View.GONE
        itemView.findViewById<TextView>(R.id.cheeseTextView).visibility = if (customCartItem.cheese) View.VISIBLE else View.GONE
        itemView.findViewById<TextView>(R.id.sizeTextView).text = customCartItem.size
        itemView.findViewById<TextView>(R.id.quantityTextView).text = customCartItem.quantity.toString()
        itemView.findViewById<TextView>(R.id.customCartPriceTextView).text = "£${"%.2f".format(customCartItem.price * customCartItem.quantity)}"





    }
}



// ViewHolder for PremadeCartItem
class PremadeLastOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @SuppressLint("SetTextI18n")
    fun bind(premadeCartItem: PremadeCartItem) {
        // Bind data to views
        itemView.findViewById<TextView>(R.id.titleTextView).text = premadeCartItem.item.title
        itemView.findViewById<TextView>(R.id.cartQuantityReadyMade).text = premadeCartItem.quantity.toString()
        itemView.findViewById<TextView>(R.id.premadeCartPriceTextView).text = "£${"%.2f".format(premadeCartItem.item.price * premadeCartItem.quantity)}"
        // Bind other views here
    }
}