package com.example.frontend.views


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.viewmodel.ProductViewModel
@Composable
fun HomeView(viewModel: ProductViewModel = viewModel(),
             navController: NavController) {

    val state = viewModel.state

    Scaffold(
        containerColor = Color(0xFF121212),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addProduct")
                },
                containerColor = Color(0xFF1B5E20)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Products",
                fontSize = 26.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator(color = Color(0xFF4CAF50))
            }

            state.error?.let {
                Text(text = it, color = Color.Red)
            }

            LazyColumn {
                items(state.products) { product ->
                    ProductCardView(product)
                }
            }
        }
    }
}