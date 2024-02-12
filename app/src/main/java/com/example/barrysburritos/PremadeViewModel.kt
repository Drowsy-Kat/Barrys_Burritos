package com.example.barrysburritos

import android.content.Context
import android.util.Log

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException



class PremadeViewModel : ViewModel() {
    private val _premadeItems = mutableListOf<PremadeItem>()

    val premadeItems: List<PremadeItem> get() = _premadeItems


    // Function to load data from JSON file
    fun loadPremadeItems(context: Context) {
        try {
            val inputStream = context.assets.open("premade.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, Charsets.UTF_8)

            // Using Gson to parse JSON to List of PremadeItem
            val listType = object : TypeToken<List<PremadeItem>>() {}.type
            val items: List<PremadeItem> = Gson().fromJson(json, listType)

            // Assigning parsed data to _premadeItems
            _premadeItems.clear()
            _premadeItems.addAll(items)
            Log.d("Ready_Made", "Premade List: $premadeItems")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

