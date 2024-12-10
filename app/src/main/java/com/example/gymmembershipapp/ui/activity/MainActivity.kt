package com.example.gymmembershipapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.gymmembershipapp.R
import com.example.gymmembershipapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Bottom Navigation with NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        // Set up the BottomNavigationView with the NavController
        binding.bottomBar.setupWithNavController(navController)

        NavigationUI.setupWithNavController(binding.bottomBar,navController)
         //Initial fragment setup (optional, as NavController will handle it)
        if (savedInstanceState == null) {
            navController.navigate(R.id.homeFragment)  // Navigate to the HomeFragment initially
        }

          navHostFragment.navController.addOnDestinationChangedListener{_, destination, _ ->

              when(destination.id){
                  R.id.memberShipDetailFragment,
                      ->{
                          binding.bottomBar.visibility=View.GONE
                      }
                  else->{
                      binding.bottomBar.visibility=View.VISIBLE
                  }
              }

          }
        // Handle Bottom Navigation item selection
        binding.bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.profile -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                R.id.cart ->{
                    navController.navigate(R.id.cartFragment)
                    true
                }
                else -> false
            }
        }

    }

}