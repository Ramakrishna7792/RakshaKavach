package com.safety.rakshakavach.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safety.rakshakavach.viewmodel.MainViewModel

@Composable
fun ResetPasswordScreen(
    workerId: String,
    onResetSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: MainViewModel
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val error by viewModel.loginError.collectAsState()
    val safetyYellow = Color(0xFFFFCC00)
    val isPasswordValid = newPassword.length >= 6 && 
                        newPassword.any { it.isDigit() } && 
                        newPassword.any { it.isUpperCase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(safetyYellow)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                color = Color.Black,
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = safetyYellow
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "RESET PASSWORD",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Text(
                "Create a new secure password",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Back Button
                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (error != null) {
                    Text(
                        text = error!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Identity Verified Banner
                Surface(
                    color = Color(0xFFC8E6C9),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Identity Verified - Worker ID: $workerId",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // New Password
                FieldLabel("NEW PASSWORD")
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { 
                        newPassword = it 
                        viewModel.clearErrors()
                    },
                    placeholder = { Text("Enter new password", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(
                                imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )

                AnimatedVisibility(visible = newPassword.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(
                                color = if (isPasswordValid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Password Requirements:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isPasswordValid) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        RequirementRow("At least 6 characters", newPassword.length >= 6, isPasswordValid)
                        RequirementRow("At least 1 uppercase letter", newPassword.any { it.isUpperCase() }, isPasswordValid)
                        RequirementRow("At least 1 number", newPassword.any { it.isDigit() }, isPasswordValid)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password
                FieldLabel("CONFIRM NEW PASSWORD")
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Re-enter new password", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.resetPassword(workerId, newPassword, onResetSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = isPasswordValid && (newPassword == confirmPassword)
                ) {
                    Text(
                        "UPDATE PASSWORD",
                        fontWeight = FontWeight.Bold,
                        color = safetyYellow,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RequirementRow(text: String, isMet: Boolean, allMet: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = if (isMet) Color(0xFF2E7D32) else if (allMet) Color(0xFF2E7D32) else Color(0xFFC62828)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = if (isMet) Color(0xFF2E7D32) else if (allMet) Color(0xFF2E7D32) else Color(0xFFC62828)
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = buildAnnotatedString {
            append(text)
            append(" ")
            withStyle(SpanStyle(color = Color.Red)) {
                append("*")
            }
        },
        fontWeight = FontWeight.ExtraBold,
        fontSize = 13.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
