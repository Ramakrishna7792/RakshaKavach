package com.safety.rakshakavach.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safety.rakshakavach.viewmodel.MainViewModel

@Composable
fun ForgotPasswordScreen(
    onVerifySuccess: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: MainViewModel
) {
    var workerId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var showOtpBox by remember { mutableStateOf(false) }

    val error by viewModel.loginError.collectAsState()
    val registrationError by viewModel.registrationError.collectAsState()
    val isPhoneVerified by viewModel.isPhoneVerified.collectAsState()
    val context = LocalContext.current
    val safetyYellow = Color(0xFFFFCC00)

    LaunchedEffect(isPhoneVerified) {
        if (isPhoneVerified) {
            onVerifySuccess(workerId)
        }
    }

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
                "FORGOT PASSWORD?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Text(
                "Verify your identity to reset",
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
                    .verticalScroll(rememberScrollState())
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
                        Text("Back to Login", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (error != null || registrationError != null) {
                    Text(
                        text = error ?: registrationError ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Info Banner
                Surface(
                    color = Color(0xFFFFE0B2),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Enter your Worker ID and registered phone number to verify your identity",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Worker ID Field
                FieldLabel("WORKER ID")
                OutlinedTextField(
                    value = workerId,
                    onValueChange = { 
                        workerId = it 
                        viewModel.clearErrors()
                    },
                    placeholder = { Text("e.g., RKSH9876", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )
                Text(
                    "Enter your unique 8-character ID",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Phone Number Field
                FieldLabel("REGISTERED PHONE NUMBER")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { 
                            phoneNumber = it.filter { char -> char.isDigit() }
                            viewModel.clearErrors()
                        },
                        placeholder = { Text("", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        enabled = !isPhoneVerified,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { 
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                                Icon(Icons.Outlined.Phone, contentDescription = null)
                                Text(" +91", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            disabledBorderColor = Color(0xFF2E7D32)
                        )
                    )
                    
                    if (!isPhoneVerified) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { 
                                viewModel.verifyIdentity(workerId, "+91$phoneNumber") {
                                    viewModel.sendOtp("+91$phoneNumber", context as Activity)
                                    showOtpBox = true
                                }
                            },
                            enabled = phoneNumber.length >= 10 && workerId.length >= 4,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("VERIFY", fontSize = 12.sp, color = safetyYellow)
                        }
                    }
                }

                AnimatedVisibility(visible = showOtpBox && !isPhoneVerified) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        FieldLabel("ENTER OTP")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = otp,
                                onValueChange = { if (it.length <= 6) otp = it },
                                placeholder = { Text("6-digit OTP", color = Color.Gray) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.verifyOtp(otp) },
                                enabled = otp.length == 6,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("OK", color = safetyYellow)
                            }
                        }
                        Text(
                            "Check your SMS for the verification code",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bottom Banner
                Surface(
                    color = Color.Black,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = safetyYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Identity verification protects your account from unauthorized access",
                            fontSize = 11.sp,
                            color = safetyYellow,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
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
