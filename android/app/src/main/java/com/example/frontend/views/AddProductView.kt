package com.example.frontend.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.model.ProductModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController


@Composable
fun AddProductView(onAdd: (ProductModel) -> Unit,navController: NavController) {

    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {

        Text("Add Product", color = Color.White, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

        TextField(
            value = sku,
            onValueChange = { sku = it },
            label = { Text("SKU") }
        )

        TextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val product = ProductModel(
                    id = 0,
                    name = name,
                    sku = sku,
                    quantity = 0,
                    price = price.toFloatOrNull() ?: 0f,
                    description = null
                )
                onAdd(product)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1B5E20)
            )
        ) {
            Text("Save")
        }
    }
}