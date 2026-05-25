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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun OrderDetailsView(
    orderId: Int,
    orderViewModel: OrderViewModel,
    productViewModel: ProductViewModel,
    navController: NavController
) {
    val orderState = orderViewModel.state
    val products = productViewModel.state.products
    val order = orderState.orders.firstOrNull { it.id == orderId }
    val context = LocalContext.current
    val token = TokenManager.getToken(context)
    val background = Color(0xFF111513)
    val surface = Color(0xFF1B221E)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)

    LaunchedEffect(token, orderId) {
        if (token == null) {
            navController.navigate("login") {
                popUpTo("orderDetails/$orderId") { inclusive = true }
            }
        } else {
            orderViewModel.loadCurrentUser(token)
            if (order == null) {
                orderViewModel.loadOrders(token)
            }
            if (products.isEmpty()) {
                productViewModel.loadProducts(token)
            }
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

        if (order == null) {
            Text(
                text = "Order not found",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            orderState.error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = Color(0xFFFF8A80), fontSize = 14.sp)
            }
            return@Column
        }

        OrderHeader(order = order)

        Spacer(modifier = Modifier.height(20.dp))

        OrderItemsCard(order = order, products = products)

        Spacer(modifier = Modifier.height(18.dp))

        OrderSummaryCard(order = order, products = products)

        Spacer(modifier = Modifier.height(18.dp))

        StatusTimeline(currentStatus = order.status)

        Spacer(modifier = Modifier.height(18.dp))

        if (orderState.currentUserRole == "admin") {
            StatusControls(
                currentStatus = order.status,
                onStatusChange = { status ->
                    val currentToken = TokenManager.getToken(context)
                    if (currentToken != null) {
                        orderViewModel.updateOrderStatus(order.id, status, currentToken)
                    }
                }
            )
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = surface,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Only admin users can change order status.",
                    color = muted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        orderState.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = Color(0xFFFF8A80), fontSize = 14.sp)
        }
    }
}

@Composable
private fun OrderHeader(order: OrderModel) {
    val surface = Color(0xFF1B221E)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Order #${order.id}",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = order.createdAt.replace("T", " ").take(19),
                color = muted,
                fontSize = 14.sp
            )
        }

        Surface(
            color = surface,
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 6.dp
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                contentDescription = "Order",
                tint = green,
                modifier = Modifier.padding(12.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Surface(
        color = statusColor(order.status).copy(alpha = 0.18f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = order.status,
            color = statusColor(order.status),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun OrderSummaryCard(
    order: OrderModel,
    products: List<ProductModel>
) {
    val surface = Color(0xFF1B221E)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)
    val productById = products.associateBy { it.id }
    val totalItems = order.items.sumOf { it.quantity }
    val totalValue = order.items.sumOf { item ->
        ((productById[item.productId]?.price ?: 0f) * item.quantity).toDouble()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = surface,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Items", color = muted, fontSize = 13.sp)
                Text(
                    text = totalItems.toString(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Estimated value", color = muted, fontSize = 13.sp)
                Text(
                    text = "$${String.format("%.2f", totalValue)}",
                    color = green,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OrderItemsCard(
    order: OrderModel,
    products: List<ProductModel>
) {
    val surface = Color(0xFF1B221E)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)
    val productById = products.associateBy { it.id }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Items",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            order.items.forEach { item ->
                val product = productById[item.productId]
                val productName = product?.name ?: "Product #${item.productId}"
                val lineValue = product?.let { it.price * item.quantity }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = productName, color = Color.White, fontSize = 15.sp)
                        Text(text = "Product ID ${item.productId}", color = muted, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "x${item.quantity}",
                            color = green,
                            fontWeight = FontWeight.Bold
                        )
                        if (lineValue != null) {
                            Text(
                                text = "$${lineValue}",
                                color = muted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun StatusControls(
    currentStatus: String,
    onStatusChange: (String) -> Unit
) {
    val surface = Color(0xFF1B221E)
    val muted = Color(0xFFA7B2AA)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = surface,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Change status",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Available for admin users.",
                color = muted,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(orderStatuses) { status ->
                    StatusButton(
                        status = status,
                        isSelected = status == currentStatus,
                        onClick = { onStatusChange(status) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusTimeline(currentStatus: String) {
    val surface = Color(0xFF1B221E)
    val muted = Color(0xFFA7B2AA)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = surface,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Status timeline",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            orderStatuses.forEach { status ->
                val active = orderStatuses.indexOf(status) <= orderStatuses.indexOf(currentStatus)
                    && currentStatus != "CANCELLED"
                val color = if (active || status == currentStatus) statusColor(status) else muted
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = status, color = color, fontSize = 14.sp)
                    Text(
                        text = if (status == currentStatus) "current" else if (active) "done" else "",
                        color = color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusButton(
    status: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = statusColor(status)

    Surface(
        color = if (isSelected) color.copy(alpha = 0.25f) else Color(0xFF232B26),
        shape = RoundedCornerShape(8.dp)
    ) {
        TextButton(onClick = onClick) {
            Text(
                text = status,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private val orderStatuses = listOf(
    "PENDING",
    "PACKING",
    "SHIPPED",
    "DELIVERED",
    "CANCELLED"
)

private fun statusColor(status: String): Color {
    return when (status) {
        "DELIVERED" -> Color(0xFF7BC88A)
        "SHIPPED" -> Color(0xFF80CBC4)
        "PACKING" -> Color(0xFFFFD166)
        "CANCELLED" -> Color(0xFFFF8A80)
        else -> Color(0xFFA7B2AA)
    }
}
