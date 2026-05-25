package com.example.frontend.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.navigation.NavController
import com.example.frontend.data.TokenManager
import com.example.frontend.model.OrderModel
import com.example.frontend.model.ProductModel
import com.example.frontend.viewmodel.OrderViewModel
import com.example.frontend.viewmodel.ProductViewModel

@Composable
fun OrdersView(
    orderViewModel: OrderViewModel,
    productViewModel: ProductViewModel,
    navController: NavController
) {
    val orderState = orderViewModel.state
    val products = productViewModel.state.products
    val context = LocalContext.current
    val token = TokenManager.getToken(context)
    val background = Color(0xFF111513)
    val surface = Color(0xFF1B221E)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)

    LaunchedEffect(token) {
        if (token == null) {
            navController.navigate("login") {
                popUpTo("orders") { inclusive = true }
            }
        } else {
            orderViewModel.loadCurrentUser(token)
            orderViewModel.loadOrders(token)
            productViewModel.loadProducts(token)
        }
    }

    Scaffold(
        containerColor = background,
        bottomBar = {
            AppBottomBar(selectedRoute = "orders", navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("createOrder") },
                containerColor = green,
                contentColor = Color(0xFF0E130F),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create order")
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
                        text = "Orders",
                        fontSize = 30.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Create and track warehouse orders",
                        fontSize = 14.sp,
                        color = muted
                    )
                }

                Surface(
                    color = surface,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 6.dp
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = "Orders",
                        tint = green,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (orderState.isLoading) {
                CircularProgressIndicator(color = green)
            }

            orderState.error?.let {
                Text(text = it, color = Color(0xFFFF8A80))
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (!orderState.isLoading && orderState.orders.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = surface,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "No orders yet",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Use the add button to create the first order from available products.",
                            color = muted,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(orderState.orders) { order ->
                    OrderCard(
                        order = order,
                        products = products,
                        onClick = {
                            navController.navigate("orderDetails/${order.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: OrderModel,
    products: List<ProductModel>,
    onClick: () -> Unit
) {
    val surface = Color(0xFF1B221E)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)
    val productById = products.associateBy { it.id }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Order #${order.id}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.createdAt.take(10),
                        color = muted,
                        fontSize = 13.sp
                    )
                }

                Surface(
                    color = statusColor(order.status).copy(alpha = 0.18f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.status,
                        color = statusColor(order.status),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            order.items.forEach { item ->
                val productName = productById[item.productId]?.name ?: "Product #${item.productId}"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = productName, color = muted, fontSize = 14.sp)
                    Text(
                        text = "x${item.quantity}",
                        color = green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Open details",
                color = green,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun statusColor(status: String): Color {
    return when (status) {
        "DELIVERED" -> Color(0xFF7BC88A)
        "SHIPPED" -> Color(0xFF80CBC4)
        "PACKING" -> Color(0xFFFFD166)
        "CANCELLED" -> Color(0xFFFF8A80)
        else -> Color(0xFFA7B2AA)
    }
}
