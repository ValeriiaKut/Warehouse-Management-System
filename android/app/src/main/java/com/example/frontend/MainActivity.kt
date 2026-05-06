package com.example.frontend

import com.example.frontend.views.LoginView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend.views.HomeView
import com.example.frontend.views.RegisterView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginView(navController) }
                composable("register") { RegisterView(navController) }
                composable ( "home") { HomeView(navController) }
            } }
    }
}

