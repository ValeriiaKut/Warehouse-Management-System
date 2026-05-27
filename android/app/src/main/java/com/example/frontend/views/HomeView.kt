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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.data.TokenManager
import com.example.frontend.model.ProductModel
import com.example.frontend.viewmodel.OrderViewModel
import com.example.frontend.viewmodel.ProductViewModel

@Composable
fun HomeView(
    viewModel: ProductViewModel = viewModel(),
    orderViewModel: OrderViewModel,
    navController: NavController
) {
    val state = viewModel.state
    val canManageProducts = orderViewModel.state.currentUserRole == "admin"
    val background = Color(0xFF111513)
    val surface = Color(0xFF1B221E)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)
    val context = LocalContext.current
    val token = TokenManager.getToken(context)
    var search by remember { mutableStateOf("") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var lowStockOnly by remember { mutableStateOf(false) }
    var productPendingDelete by remember { mutableStateOf<ProductModel?>(null) }

    LaunchedEffect(token, search, minPrice, maxPrice, lowStockOnly) {
        if (token == null) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        } else {
            orderViewModel.loadCurrentUser(token)
            viewModel.loadProducts(
                token = token,
                search = search,
                minPrice = minPrice.toFloatOrNull(),
                maxPrice = maxPrice.toFloatOrNull(),
                lowStock = lowStockOnly
            )
        }
    }

    Scaffold(
        containerColor = background,
        bottomBar = {
            AppBottomBar(selectedRoute = "home", navController = navController)
        },
        floatingActionButton = {
            if (canManageProducts) {
                FloatingActionButton(
                    onClick = { navController.navigate("addProduct") },
                    containerColor = green,
                    contentColor = Color(0xFF0E130F),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add product")
                }
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

            ProductFilters(
                search = search,
                onSearchChange = { search = it },
                minPrice = minPrice,
                onMinPriceChange = { minPrice = it },
                maxPrice = maxPrice,
                onMaxPriceChange = { maxPrice = it },
                lowStockOnly = lowStockOnly,
                onLowStockChange = { lowStockOnly = it },
                onClear = {
                    search = ""
                    minPrice = ""
                    maxPrice = ""
                    lowStockOnly = false
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (state.isLoading) {
                CircularProgressIndicator(color = green)
            }

            state.error?.let {
                Text(text = it, color = Color(0xFFFF8A80))
            }

            state.successMessage?.let {
                Text(text = it, color = green)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (!state.isLoading && state.products.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = surface,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "No products found",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Try clearing filters or add products as an admin.",
                            color = muted,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.products) { product ->
                    ProductCardView(
                        product = product,
                        canManageProducts = canManageProducts,
                        onEdit = {
                            navController.navigate("editProduct/${it.id}")
                        },
                        onDelete = {
                            productPendingDelete = it
                        }
                    )
                }
            }
        }
    }

    productPendingDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productPendingDelete = null },
            title = { Text("Delete product") },
            text = { Text("Delete ${product.name}? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        val currentToken = TokenManager.getToken(context)
                        if (currentToken != null) {
                            viewModel.deleteProduct(product.id, currentToken)
                        }
                        productPendingDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { productPendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProductFilters(
    search: String,
    onSearchChange: (String) -> Unit,
    minPrice: String,
    onMinPriceChange: (String) -> Unit,
    maxPrice: String,
    onMaxPriceChange: (String) -> Unit,
    lowStockOnly: Boolean,
    onLowStockChange: (Boolean) -> Unit,
    onClear: () -> Unit
) {
    val surface = Color(0xFF1B221E)
    val field = Color(0xFF232B26)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = surface,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            FilterTextField(
                value = search,
                onValueChange = onSearchChange,
                label = "Search by name",
                field = field,
                green = green,
                muted = muted
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterTextField(
                    value = minPrice,
                    onValueChange = { onMinPriceChange(it.filter { char -> char.isDigit() || char == '.' }) },
                    label = "Min price",
                    field = field,
                    green = green,
                    muted = muted,
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
                FilterTextField(
                    value = maxPrice,
                    onValueChange = { onMaxPriceChange(it.filter { char -> char.isDigit() || char == '.' }) },
                    label = "Max price",
                    field = field,
                    green = green,
                    muted = muted,
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = lowStockOnly,
                        onCheckedChange = onLowStockChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = green,
                            checkedTrackColor = Color(0xFF26362A),
                            uncheckedThumbColor = muted,
                            uncheckedTrackColor = Color(0xFF232B26)
                        )
                    )
                    Text(
                        text = "Low stock only",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                TextButton(onClick = onClear) {
                    Text(text = "Clear", color = green, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FilterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    field: Color,
    green: Color,
    muted: Color,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier,
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
