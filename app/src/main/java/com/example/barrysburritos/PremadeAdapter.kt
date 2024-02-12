package com.example.barrysburritos

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.nio.charset.Charset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PremadeAdapter(
    private val premadeList: List<PremadeItem>,
    private val premadeCartViewModel: PremadeCartViewModel,
    private val fragment: Fragment
) : RecyclerView.Adapter<PremadeAdapter.PremadeViewHolder>() {




    inner class PremadeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.premadeTitle)
        val imageView: ImageView = itemView.findViewById(R.id.premadeImage)
        val priceTextView: TextView = itemView.findViewById(R.id.premadeCost)
        val button: Button = itemView.findViewById(R.id.premadeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PremadeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.readymade_item, parent, false)
        return PremadeViewHolder(view)

    }

    override fun onBindViewHolder(holder: PremadeViewHolder, position: Int) {


        val premadeItem = premadeList[position]


        // Bind data to your view components here
        holder.titleTextView.text = premadeItem.title
        holder.priceTextView.text = "£${ String.format("%.2f", premadeItem.price)}"

        // Use getImageResourceByName to load image dynamically
        holder.imageView.setImageResource(
            getImageResourceByName(
                holder.itemView.context,
                premadeItem.imageName
            )
        )

        holder.itemView.setOnClickListener {
            // Do something when the entire box is clicked
            // For example, show a toast
            showPopup(holder.itemView.context, premadeItem)
        }

        // Set click listener on the button inside the box
        holder.button.setOnClickListener {
            // Do something when the button is clicked
            // For example, show a different toast


            var premadeCartItem = PremadeCartItem(premadeItem, 1)
            premadeCartViewModel.addToCart(premadeCartItem)

            Toast.makeText(holder.itemView.context, "Button Clicked: ${premadeItem.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return premadeList.size
    }

    private fun getImageResourceByName(context: Context, imageName: String): Int {
        // Resolve image resource ID dynamically
        return context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )
    }

    private fun showPopup(context: Context, premadeItem: PremadeItem) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popup_readymade)

        // Update views in the popup with data from the clicked BoxItem
        val titleTextView = dialog.findViewById<TextView>(R.id.popupTitle)
        val descriptionTextView = dialog.findViewById<TextView>(R.id.popupDescription)
        val imageView = dialog.findViewById<ImageView>(R.id.popupImage)
        val priceTextView = dialog.findViewById<TextView>(R.id.popupCost)

        titleTextView.text = premadeItem.title
        descriptionTextView.text = premadeItem.description
        priceTextView.text = "£ ${ String.format("%.2f", premadeItem.price)}"
        val imageName = premadeItem.imageName
        val resourceId = getImageResourceByName(context, imageName)
        imageView.setImageResource(resourceId)

        val popupButton = dialog.findViewById<Button>(R.id.popupButton)

        val increaseButton = dialog.findViewById<FloatingActionButton>(R.id.popupIncreaseButton)
        val decreaseButton = dialog.findViewById<FloatingActionButton>(R.id.popupDecreaseButton)
        val quantityTextView = dialog.findViewById<TextView>(R.id.quantityPopupTextView)

        var quantity = 1
        quantityTextView.text = quantity.toString()

        increaseButton.setOnClickListener {
            quantity++
            quantityTextView.text = quantity.toString()
        }

        decreaseButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityTextView.text = quantity.toString()
            }
        }


        // Set OnClickListener for the button
        popupButton.setOnClickListener {
            // Handle button click here
            Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show()
            var premadeCartItem = PremadeCartItem(premadeItem, quantity )
            premadeCartViewModel.addToCart(premadeCartItem)
            // You can dismiss the dialog if needed
            dialog.dismiss()
        }
        // Show the pop-up
        dialog.show()
    }

    companion object {
        fun ssssssssssssssssssssssssssssssss(context: Context, fileName: String): List<PremadeItem> {
            val json: String = try {
                context.assets.open(fileName).bufferedReader(Charset.defaultCharset()).use {
                    it.readText()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return emptyList()//todo: get rid of this
            }

            val listType = object : TypeToken<List<PremadeItem>>() {}.type
            return Gson().fromJson(json, listType)
        }
    }


}
