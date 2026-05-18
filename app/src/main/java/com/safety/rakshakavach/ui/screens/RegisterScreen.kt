package com.safety.rakshakavach.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import android.app.Activity
import com.safety.rakshakavach.R
import com.safety.rakshakavach.viewmodel.MainViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: MainViewModel
) {
    var name by remember { mutableStateOf("") }
    var workplace by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var showOtpBox by remember { mutableStateOf(false) }

    val registrationSuccess by viewModel.registrationSuccess.collectAsState()
    val registrationError by viewModel.registrationError.collectAsState()
    val isPhoneVerified by viewModel.isPhoneVerified.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val getT = { key: String -> com.safety.rakshakavach.ui.Translations.getString(key, currentLang) }
    val context = LocalContext.current
    
    LaunchedEffect(registrationSuccess) {
        registrationSuccess?.let { 
            onRegisterSuccess(it)
            viewModel.resetRegistrationState()
        }
    }

    val safetyYellow = Color(0xFFFFCC00)
    val isPasswordValid = password.length >= 8 && 
                        password.any { it.isDigit() } && 
                        password.any { it.isUpperCase() } &&
                        password.any { !it.isLetterOrDigit() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(safetyYellow)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = safetyYellow
                )
            }
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
                Text(
                    getT("create_account"),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                if (registrationError != null) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(registrationError!!, color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Full Name Field
                FieldLabel(getT("full_name"))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(getT("full_name"), color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Workplace Field
                FieldLabel(getT("company_org"))
                OutlinedTextField(
                    value = workplace,
                    onValueChange = { workplace = it },
                    placeholder = { Text(getT("your_workplace"), color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Phone Field
                FieldLabel(getT("phone_number"))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { 
                            if (!isPhoneVerified) {
                                phone = it.filter { char -> char.isDigit() }
                                viewModel.clearErrors()
                            }
                        },
                        placeholder = { Text(getT("registered_phone"), color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        enabled = !isPhoneVerified,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { 
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                                Icon(Icons.Default.Phone, contentDescription = null)
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
                                viewModel.sendOtp("+91$phone", context as Activity)
                                showOtpBox = true
                            },
                            enabled = phone.length >= 10,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text(getT("verify"), fontSize = 12.sp, color = safetyYellow)
                        }
                    }
                }

                if (isPhoneVerified) {
                    Text(
                        "✓ ${getT("phone_verified")}",
                        color = Color(0xFF2E7D32),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                AnimatedVisibility(visible = showOtpBox && !isPhoneVerified) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        FieldLabel(getT("enter_otp"))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = otp,
                                onValueChange = { if (it.length <= 6) otp = it },
                                placeholder = { Text(getT("otp_placeholder"), color = Color.Gray) },
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
                                Text(getT("ok"), color = safetyYellow)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Set Password Field
                FieldLabel(getT("set_password"))
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it 
                        viewModel.clearErrors()
                    },
                    placeholder = { Text(getT("enter_password"), color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )

                AnimatedVisibility(visible = password.isNotEmpty()) {
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
                            text = getT("password_req"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isPasswordValid) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        RequirementRow(getT("req_chars"), password.length >= 8, isPasswordValid)
                        RequirementRow(getT("req_upper"), password.any { it.isUpperCase() }, isPasswordValid)
                        RequirementRow(getT("req_num"), password.any { it.isDigit() }, isPasswordValid)
                        RequirementRow(getT("req_special"), password.any { !it.isLetterOrDigit() }, isPasswordValid)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password Field
                FieldLabel(getT("confirm_password"))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text(getT("re_enter_password"), color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
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

                Spacer(modifier = Modifier.height(24.dp))

                // Terms and Pledge Box
                Surface(
                    color = safetyYellow.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, safetyYellow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = agreedToTerms,
                                onCheckedChange = { agreedToTerms = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color.Black)
                            )
                            Text(
                                text = buildAnnotatedString {
                                    append(getT("agree_terms"))
                                    append(" ")
                                    withStyle(SpanStyle(color = Color.Red)) {
                                        append("*")
                                    }
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Row(
                            modifier = Modifier.padding(start = 12.dp, bottom = 8.dp, end = 12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp).padding(top = 2.dp),
                                tint = Color(0xFF8B4513)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                getT("safety_pledge"),
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.register(name, workplace, "+91$phone", password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = isPhoneVerified && agreedToTerms && name.isNotBlank() && workplace.isNotBlank() && phone.length >= 10 && isPasswordValid && (password == confirmPassword)
                ) {
                    Text(
                        getT("register_now"),
                        fontWeight = FontWeight.Bold,
                        color = safetyYellow,
                        fontSize = 18.sp
                    )
                }

                TextButton(
                    onClick = onBackToLogin,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                ) {
                    Text(getT("already_account"), color = Color.Gray)
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
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}
