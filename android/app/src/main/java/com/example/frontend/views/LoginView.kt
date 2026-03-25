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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun LoginView(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val darkGray = Color(0xFF3D3D3F)
    val gray = Color(0xFF464545)
    val lightGreen = Color(0xFF587059)




    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Welcome.",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))


            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Warehouse Inventory Management System.",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "I don't have account yet.",
                    color = Color(0xFF587059),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            navController.navigate("register")
                        }
                        .padding(end = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))



            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = gray,
                    unfocusedContainerColor = gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = lightGreen,
                    unfocusedLabelColor = Color.White,
                    focusedLabelColor = Color.White,
                )
            )


            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = gray,
                    unfocusedContainerColor = gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = lightGreen,
                    unfocusedLabelColor = Color.White,
                    focusedLabelColor = Color.White,
                ),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image,
                            contentDescription = if (passwordVisible)
                                "Hide password" else "Show password",
                            tint = lightGreen)
                    }
                }
            )

            Spacer(modifier = Modifier.height(30.dp))


            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightGreen
                )
            ) {
                Text("Sign In", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

