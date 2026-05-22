package com.example.frontend.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.data.TokenManager
import com.example.frontend.viewmodel.ProductViewModel

@Composable
fun HomeView(
    viewModel: ProductViewModel = viewModel(),
    navController: NavController
) {
    val state = viewModel.state
    val background = Color(0xFF111513)
    val surface = Color(0xFF1B221E)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)
    val context = LocalContext.current
    val token = TokenManager.getToken(context)

    LaunchedEffect(token) {
        if (token == null) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        } else {
            viewModel.loadProducts(token)
        }
    }

    Scaffold(
        containerColor = background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addProduct") },
                containerColor = green,
                contentColor = Color(0xFF0E130F),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add product")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF202A24), background, Color(0xFF0D100F))
                    )
                )
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Inventory",
                        fontSize = 30.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Track stock, SKU and pricing",
                        fontSize = 14.sp,
                        color = muted
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = surface,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 6.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = "Inventory",
                            tint = green,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Surface(
                        color = surface,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 6.dp
                    ) {
                        IconButton(
                            onClick = {
                                TokenManager.clearToken(context)
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = green
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = surface,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Products", color = muted, fontSize = 13.sp)
                        Text(
                            text = state.products.size.toString(),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Total stock", color = muted, fontSize = 13.sp)
                        Text(
                            text = state.products.sumOf { it.quantity }.toString(),
                            color = green,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (state.isLoading) {
                CircularProgressIndicator(color = green)
            }

            state.error?.let {
                Text(text = it, color = Color(0xFFFF8A80))
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.products) { product ->
                    ProductCardView(
                        product = product,
                        onEdit = {
                            navController.navigate("editProduct/${it.id}")
                        },
                        onDelete = {
                            val currentToken = TokenManager.getToken(context)
                            if (currentToken != null) {
                                viewModel.deleteProduct(it.id, currentToken)
                            }
                        }
                    )
                }
            }
        }
    }
}
