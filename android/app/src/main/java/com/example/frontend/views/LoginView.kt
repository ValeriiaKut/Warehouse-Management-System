package com.example.frontend.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.data.TokenManager
import com.example.frontend.model.LoginModel
import com.example.frontend.viewmodel.LoginViewModel

@Composable
fun LoginView(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val viewModel: LoginViewModel = viewModel()
    val context = LocalContext.current

    val background = Color(0xFF111513)
    val surface = Color(0xFF1B221E)
    val field = Color(0xFF232B26)
    val green = Color(0xFF7BC88A)
    val deepGreen = Color(0xFF4F7D57)
    val muted = Color(0xFFA7B2AA)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF27342C), background, Color(0xFF0D100F))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Welcome back.",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Sign in to manage your warehouse inventory.",
                fontSize = 16.sp,
                color = muted
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "I don't have an account",
                color = green,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { navController.navigate("register") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
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

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                ),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = muted
                        )
                    }
                }
            )

            viewModel.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                val retryText = if (viewModel.retrySeconds > 0) {
                    " Try again in ${viewModel.retrySeconds}s."
                } else {
                    ""
                }
                Text(text = message + retryText, color = Color(0xFFFF8A80), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        email.isBlank() -> {
                            viewModel.errorMessage = "Enter your email."
                        }
                        password.isBlank() -> {
                            viewModel.errorMessage = "Enter your password."
                        }
                        else -> {
                            viewModel.login(
                                loginData = LoginModel(email, password)
                            ) { token ->
                                TokenManager.saveToken(context, token)
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                enabled = !viewModel.isLoading && viewModel.retrySeconds == 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    disabledContainerColor = deepGreen,
                    contentColor = Color(0xFF0E130F)
                )
            ) {
                Text(
                    text = when {
                        viewModel.isLoading -> "Signing In..."
                        viewModel.retrySeconds > 0 -> "Try again in ${viewModel.retrySeconds}s"
                        else -> "Sign In"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}
