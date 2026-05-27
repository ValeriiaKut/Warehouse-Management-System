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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.model.RegisterModel
import com.example.frontend.viewmodel.RegisterViewModel

@Composable
fun RegisterView(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordConfirmVisible by remember { mutableStateOf(false) }
    val viewModel: RegisterViewModel = viewModel()

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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {


            Text(
                text = "Create account",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Set up your warehouse workspace.",
                fontSize = 16.sp,
                color = muted
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "I already have an account",
                color = green,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { navController.navigate("login") }
            )

            Spacer(modifier = Modifier.height(28.dp))

            AuthTextField(value = name, onValueChange = { name = it }, label = "Name")
            Spacer(modifier = Modifier.height(12.dp))
            AuthTextField(value = email, onValueChange = { email = it }, label = "Email")
            Spacer(modifier = Modifier.height(12.dp))

            AuthPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                visible = passwordVisible,
                onVisibleChange = { passwordVisible = !passwordVisible },
                field = field,
                green = green,
                muted = muted
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordRequirement(
                text = "At least 8 characters",
                isMet = password.length >= 8
            )
            PasswordRequirement(
                text = "Contains a number",
                isMet = password.any { it.isDigit() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AuthPasswordField(
                value = passwordConfirm,
                onValueChange = { passwordConfirm = it },
                label = "Confirm Password",
                visible = passwordConfirmVisible,
                onVisibleChange = { passwordConfirmVisible = !passwordConfirmVisible },
                field = field,
                green = green,
                muted = muted
            )

            viewModel.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = message, color = Color(0xFFFF8A80), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    when {
                        name.isBlank() -> {
                            viewModel.errorMessage = "Enter your name."
                        }
                        email.isBlank() -> {
                            viewModel.errorMessage = "Enter your email."
                        }
                        password.length < 8 -> {
                            viewModel.errorMessage = "Password must be at least 8 characters long."
                        }
                        password.none { it.isDigit() } -> {
                            viewModel.errorMessage = "Password must contain at least one number."
                        }
                        password != passwordConfirm -> {
                            viewModel.errorMessage = "Passwords do not match."
                        }
                        else -> {
                            viewModel.register(
                                registerData = RegisterModel(
                                    email = email,
                                    password = password,
                                    username = name
                                )
                            ) {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                enabled = !viewModel.isLoading,
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
                    text = if (viewModel.isLoading) "Signing Up..." else "Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}

@Composable
private fun PasswordRequirement(
    text: String,
    isMet: Boolean
) {
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)

    Text(
        text = text,
        color = if (isMet) green else muted,
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
    )
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    val field = Color(0xFF232B26)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
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
}

@Composable
private fun AuthPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onVisibleChange: () -> Unit,
    field: Color,
    green: Color,
    muted: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
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
            val icon = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(onClick = onVisibleChange) {
                Icon(
                    imageVector = icon,
                    contentDescription = if (visible) "Hide password" else "Show password",
                    tint = muted
                )
            }
        }
    )
}
