package com.example.frontend.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.model.ProductModel

@Composable
fun ProductCardView(product: ProductModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = product.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "SKU: ${product.sku}",
                color = Color.Gray
            )

            Text(
                text = "Price: $${product.price}",
                color = Color(0xFF4CAF50)
            )

            Text(
                text = "Qty: ${product.quantity}",
                color = Color.LightGray
            )

            product.description?.let {
                Text(
                    text = it,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}