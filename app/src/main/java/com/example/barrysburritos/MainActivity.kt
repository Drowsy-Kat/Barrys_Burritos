package com.example.barrysburritos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.barrysburritos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var premadeViewModel: PremadeViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        premadeViewModel = ViewModelProvider(this)[PremadeViewModel::class.java]

        premadeViewModel.loadPremadeItems(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())



        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId)
            {
                R.id.Home -> replaceFragment(Home())
                R.id.Ready_Made -> replaceFragment(Ready_Made())
                R.id.Custom -> replaceFragment(Custom())
                R.id.Cart -> replaceFragment(Cart())
                else ->{}
            }
            true
        }



    }


    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
    }

    // TODO: make everything look pretty
    // TODO: annotate code
    // TODO: clean up code
    // TODO: organise file structure



}
