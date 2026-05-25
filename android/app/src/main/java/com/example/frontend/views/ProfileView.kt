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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.frontend.viewmodel.UserViewModel

@Composable
fun ProfileView(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val state = userViewModel.state
    val context = LocalContext.current
    val token = TokenManager.getToken(context)
    val background = Color(0xFF111513)
    val surface = Color(0xFF1B221E)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)

    LaunchedEffect(token) {
        if (token == null) {
            navController.navigate("login") {
                popUpTo("profile") { inclusive = true }
            }
        } else {
            userViewModel.loadCurrentUser(token)
        }
    }

    Scaffold(
        containerColor = background,
        bottomBar = {
            AppBottomBar(selectedRoute = "profile", navController = navController)
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Profile",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Current account and permissions",
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
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = green,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            CircularProgressIndicator(color = green)
        }

        state.error?.let {
            Text(text = it, color = Color(0xFFFF8A80), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        state.user?.let { user ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = surface,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    ProfileRow(label = "Username", value = user.username)
                    Spacer(modifier = Modifier.height(14.dp))
                    ProfileRow(label = "Email", value = user.email)
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Role", color = muted, fontSize = 13.sp)
                            Text(
                                text = user.role,
                                color = green,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "Role",
                            tint = green
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                TokenManager.clearToken(context)
                userViewModel.clear()
                navController.navigate("login") {
                    popUpTo("profile") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF8A80),
                contentColor = Color(0xFF1B1111)
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout"
            )
            Text(
                text = "Logout",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    val muted = Color(0xFFA7B2AA)

    Column {
        Text(text = label, color = muted, fontSize = 13.sp)
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
