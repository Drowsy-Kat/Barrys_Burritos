package com.example.barrysburritos

import android.app.Application
import androidx.lifecycle.ViewModelProvider

class BarrysBurritosApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize PremadeViewModel and load data
        val viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this@BarrysBurritosApplication).create(PremadeViewModel::class.java)

        viewModel.loadPremadeItems(this)
    }
}