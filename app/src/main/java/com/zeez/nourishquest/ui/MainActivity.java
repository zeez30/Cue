package com.zeez.nourishquest.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.zeez.nourishquest.R;
import com.zeez.nourishquest.databinding.ActivityMainBinding;

/**
 * Main entry point for the application UI.
 * Implements the Single Activity pattern using the Jetpack Navigation component.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding for the activity layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configure the navigation controller for fragment management
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Link the BottomNavigationView with the NavController
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }
    }
}