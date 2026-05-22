package com.example.frontend

import com.example.frontend.views.LoginView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontend.data.TokenManager
import com.example.frontend.ui.theme.FRONTENDTheme
import com.example.frontend.viewmodel.ProductViewModel
import com.example.frontend.views.AddProductView
import com.example.frontend.views.HomeView
import com.example.frontend.views.RegisterView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            FRONTENDTheme {
            val navController = rememberNavController()
            val startDestination = if (TokenManager.getToken(this) != null) "home" else "login"
            val productViewModel: ProductViewModel = viewModel()

            NavHost(navController = navController, startDestination = startDestination) {
                composable("login") { LoginView(navController) }
                composable("register") { RegisterView(navController) }
                composable("home") {
                    HomeView(
                        viewModel = productViewModel,
                        navController = navController
                    )
                }
                composable("addProduct") {
                    AddProductView(
                        navController = navController,
                        onAdd = { product ->
                            val token = TokenManager.getToken(this@MainActivity)
                            if (token != null) {
                                productViewModel.addProduct(product, token) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    )
                }
                composable(
                    route = "editProduct/{productId}",
                    arguments = listOf(navArgument("productId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getInt("productId")
                    val product = productViewModel.state.products.firstOrNull { it.id == productId }

                    if (product != null) {
                        AddProductView(
                            navController = navController,
                            productToEdit = product,
                            onAdd = { updatedProduct ->
                                val token = TokenManager.getToken(this@MainActivity)
                                if (token != null) {
                                    productViewModel.updateProduct(updatedProduct, token) {
                                        navController.popBackStack()
                                    }
                                }
                            }
                        )
                    }
                }
            } }
        }
    }
}
