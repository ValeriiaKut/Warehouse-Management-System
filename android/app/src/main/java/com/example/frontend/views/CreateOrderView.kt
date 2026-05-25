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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend.data.TokenManager
import com.example.frontend.model.OrderItemCreate
import com.example.frontend.model.ProductModel
import com.example.frontend.viewmodel.OrderViewModel
import com.example.frontend.viewmodel.ProductViewModel

@Composable
fun CreateOrderView(
    orderViewModel: OrderViewModel,
    productViewModel: ProductViewModel,
    navController: NavController,
    onOrderCreated: () -> Unit
) {
    val context = LocalContext.current
    val token = TokenManager.getToken(context)
    val products = productViewModel.state.products
    val availableProducts = products.filter { it.quantity > 0 }
    val quantities = remember { mutableStateMapOf<Int, String>() }
    var localError by remember { mutableStateOf<String?>(null) }
    val background = Color(0xFF111513)
    val surface = Color(0xFF1B221E)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)

    LaunchedEffect(token) {
        if (token != null && products.isEmpty()) {
            productViewModel.loadProducts(token)
        }
    }

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
            text = "Create order",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Choose products and quantities from stock.",
            color = muted,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        orderViewModel.state.error?.let {
            Text(text = it, color = Color(0xFFFF8A80), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        localError?.let {
            Text(text = it, color = Color(0xFFFF8A80), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (availableProducts.isEmpty()) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "No stock available",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Products with zero quantity cannot be ordered.",
                        color = muted,
                        fontSize = 14.sp
                    )
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(availableProducts) { product ->
                OrderProductRow(
                    product = product,
                    quantity = quantities[product.id].orEmpty(),
                    onQuantityChange = { value ->
                        localError = null
                        val cleanValue = value.filter { it.isDigit() }
                        val requestedQuantity = cleanValue.toIntOrNull()
                        quantities[product.id] = when {
                            requestedQuantity == null -> cleanValue
                            requestedQuantity > product.quantity -> product.quantity.toString()
                            else -> cleanValue
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                val items = quantities.mapNotNull { (productId, value) ->
                    val quantity = value.toIntOrNull() ?: 0
                    if (quantity > 0) OrderItemCreate(productId = productId, quantity = quantity) else null
                }

                if (items.isEmpty()) {
                    localError = "Choose at least one product quantity."
                } else if (token != null) {
                    orderViewModel.createOrder(items, token) {
                        onOrderCreated()
                    }
                }
            },
            enabled = !orderViewModel.state.isLoading && availableProducts.isNotEmpty(),
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
                text = if (orderViewModel.state.isLoading) "Creating..." else "Create Order",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun OrderProductRow(
    product: ProductModel,
    quantity: String,
    onQuantityChange: (String) -> Unit
) {
    val surface = Color(0xFF1B221E)
    val field = Color(0xFF232B26)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "SKU ${product.sku}", color = muted, fontSize = 12.sp)
                Text(text = "Available ${product.quantity}", color = green, fontSize = 13.sp)
            }

            OutlinedTextField(
                value = quantity,
                onValueChange = onQuantityChange,
                label = { Text("Qty") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(0.34f),
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
    }
}
