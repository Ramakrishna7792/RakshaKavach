package com.safety.rakshakavach.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safety.rakshakavach.R
import com.safety.rakshakavach.viewmodel.MainViewModel

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: MainViewModel,
) {
    var workerId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginError by viewModel.loginError.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val safetyYellow = Color(0xFFFFCC00)
    
    val getT = { key: String -> com.safety.rakshakavach.ui.Translations.getString(key, currentLang) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(safetyYellow)
    ) {
        // Header Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Shield Logo
            Surface(
                modifier = Modifier.size(100.dp),
                color = Color.Black,
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Shield Logo",
                        modifier = Modifier.size(80.dp),
                        tint = safetyYellow
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = getT("app_name").replace(" ", "-\n"),
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                lineHeight = 44.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = getT("worker_safety_auditor").ifBlank { "Worker Safety Auditor" },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color.Black,
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = getT("safety_first").ifBlank { "SAFETY FIRST" },
                    color = safetyYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                )
            }
        }

        // Form Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (loginError != null) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(loginError!!, color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Worker ID Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        getT("worker_id"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = workerId,
                        onValueChange = { 
                            workerId = it
                            viewModel.clearErrors()
                        },
                        placeholder = { Text(getT("worker_id"), color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedLeadingIconColor = Color.Black,
                            unfocusedLeadingIconColor = Color.Gray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Password Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        getT("password"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            viewModel.clearErrors()
                        },
                        placeholder = { Text(getT("password"), color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedLeadingIconColor = Color.Black,
                            unfocusedLeadingIconColor = Color.Gray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.login(workerId, password, onLoginSuccess)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        getT("start_shift"),
                        fontWeight = FontWeight.Bold,
                        color = safetyYellow,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = getT("forgot_password").ifBlank { "Forgot Password?" },
                    modifier = Modifier.clickable { onForgotPasswordClick() },
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline
                )

                Spacer(modifier = Modifier.weight(1f))

                Row {
                    Text(getT("new_worker").ifBlank { "New worker? " }, color = Color.Gray)
                    Text(
                        getT("register"),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onRegisterClick() },
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}
