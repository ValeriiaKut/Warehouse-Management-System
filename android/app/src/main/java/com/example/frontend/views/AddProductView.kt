package com.example.frontend.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend.model.ProductModel

@Composable
fun AddProductView(
    onAdd: (ProductModel) -> Unit,
    navController: NavController,
    productToEdit: ProductModel? = null,
    errorMessage: String? = null,
    isLoading: Boolean = false
) {
    var name by remember(productToEdit) { mutableStateOf(productToEdit?.name.orEmpty()) }
    var sku by remember(productToEdit) { mutableStateOf(productToEdit?.sku.orEmpty()) }
    var quantity by remember(productToEdit) { mutableStateOf(productToEdit?.quantity?.toString().orEmpty()) }
    var price by remember(productToEdit) { mutableStateOf(productToEdit?.price?.toString().orEmpty()) }
    var description by remember(productToEdit) { mutableStateOf(productToEdit?.description.orEmpty()) }

    val background = Color(0xFF111513)
    val field = Color(0xFF232B26)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF202A24), background, Color(0xFF0D100F))
                )
            )
            .padding(20.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = green
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (productToEdit == null) "New product" else "Edit product",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (productToEdit == null) "Add stock details to your inventory." else "Update stock details.",
            color = muted,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        ProductTextField(value = name, onValueChange = { name = it }, label = "Product name", field = field, green = green, muted = muted)
        Spacer(modifier = Modifier.height(12.dp))
        ProductTextField(value = sku, onValueChange = { sku = it }, label = "SKU", field = field, green = green, muted = muted)
        Spacer(modifier = Modifier.height(12.dp))
        ProductTextField(value = quantity, onValueChange = { quantity = it }, label = "Quantity", field = field, green = green, muted = muted)
        Spacer(modifier = Modifier.height(12.dp))
        ProductTextField(value = price, onValueChange = { price = it }, label = "Price", field = field, green = green, muted = muted)
        Spacer(modifier = Modifier.height(12.dp))
        ProductTextField(value = description, onValueChange = { description = it }, label = "Description", field = field, green = green, muted = muted)

        Spacer(modifier = Modifier.height(26.dp))

        errorMessage?.let {
            Text(text = it, color = Color(0xFFFF8A80), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                val product = ProductModel(
                    id = productToEdit?.id ?: 0,
                    name = name,
                    sku = sku,
                    quantity = quantity.toIntOrNull() ?: 0,
                    price = price.toFloatOrNull() ?: 0f,
                    description = description.takeIf { it.isNotBlank() }
                )
                onAdd(product)
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = green,
                contentColor = Color(0xFF0E130F)
            )
        ) {
            Text(
                text = when {
                    isLoading && productToEdit == null -> "Saving..."
                    isLoading -> "Updating..."
                    productToEdit == null -> "Save Product"
                    else -> "Update Product"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ProductTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    field: Color,
    green: Color,
    muted: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = field,
            unfocusedContainerColor = field,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = green,
            unfocusedBorderColor = Color(0xFF364139),
            focusedLabelColor = green,
            unfocusedLabelColor = muted,
            cursorColor = green
        )
    )
}
