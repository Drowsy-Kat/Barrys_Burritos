package com.example.barrysburritos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CartAdapter(
    private val items: MutableList<Any>,
    private val customCartViewModel: CustomCartViewModel,
    private val premadeCartViewModel: PremadeCartViewModel
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    private val CUSTOM_ITEM = 0
    private val PREMADE_ITEM = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CUSTOM_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_custom_cart, parent, false)
                CustomCartItemViewHolder(view)
            }
            PREMADE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_premade_cart, parent, false)
                PremadeCartItemViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is CustomCartItemViewHolder -> {
                val customCartItem = item as CustomCartItem

                holder.bind(customCartItem)
                holder.itemView.findViewById<FloatingActionButton>(R.id.customRemove)
                    .setOnClickListener {
                        // Remove item from the list and notify the adapter
                        customCartViewModel.removeFromCart(customCartItem)
                        items.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                    }

                holder.itemView.findViewById<FloatingActionButton>(R.id.customCartAdd)
                    .setOnClickListener {
                        customCartViewModel.increaseQuantity(customCartItem)
                        notifyItemChanged(holder.adapterPosition)
                    }

                holder.itemView.findViewById<FloatingActionButton>(R.id.customCartSubtract)
                    .setOnClickListener {

                        if (customCartItem.quantity > 1) {
                            customCartViewModel.decreaseQuantity(customCartItem)
                            notifyItemChanged(holder.adapterPosition)
                        } else {
                            customCartViewModel.removeFromCart(customCartItem)
                            items.removeAt(holder.adapterPosition)
                            notifyItemRemoved(holder.adapterPosition)
                        }
                    }


            }

            is PremadeCartItemViewHolder -> {
                val premadeCartItem = item as PremadeCartItem

                holder.bind(premadeCartItem)
                holder.itemView.findViewById<FloatingActionButton>(R.id.premadeRemove)
                    .setOnClickListener {
                        // Remove item from the list and notify the adapter
                        premadeCartViewModel.removeFromCart(premadeCartItem)
                        items.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                    }

                holder.itemView.findViewById<FloatingActionButton>(R.id.premadeCartAdd)
                    .setOnClickListener {
                        premadeCartViewModel.increaseQuantity(premadeCartItem)
                        notifyItemChanged(holder.adapterPosition)
                    }

                holder.itemView.findViewById<FloatingActionButton>(R.id.premadeCartSubtract)
                    .setOnClickListener {
                        if (premadeCartItem.quantity > 1) {
                            premadeCartViewModel.decreaseQuantity(premadeCartItem)
                            notifyItemChanged(holder.adapterPosition)

                        } else {
                            premadeCartViewModel.removeFromCart(premadeCartItem)
                            items.removeAt(holder.adapterPosition)
                            notifyItemRemoved(holder.adapterPosition)
                        }

                        notifyItemChanged(holder.adapterPosition)
                    }
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
class CustomCartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
class PremadeCartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(premadeCartItem: PremadeCartItem) {
        // Bind data to views
        itemView.findViewById<TextView>(R.id.titleTextView).text = premadeCartItem.item.title
        itemView.findViewById<TextView>(R.id.cartQuantityReadyMade).text = premadeCartItem.quantity.toString()
        itemView.findViewById<TextView>(R.id.premadeCartPriceTextView).text = "£${"%.2f".format(premadeCartItem.item.price * premadeCartItem.quantity)}"
        // Bind other views here
    }
}