package com.safety.rakshakavach.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.safety.rakshakavach.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit, viewModel: MainViewModel) {
    val userState by viewModel.currentUser.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val safetyYellow = Color(0xFFFFCC00)

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language / भाषा चुनें", fontWeight = FontWeight.Black) },
            text = {
                Box(modifier = Modifier.height(400.dp)) {
                    androidx.compose.foundation.lazy.LazyColumn {
                        items(com.safety.rakshakavach.ui.AppLanguage.entries.toTypedArray().size) { index ->
                            val lang = com.safety.rakshakavach.ui.AppLanguage.entries[index]
                            ListItem(
                                headlineContent = { Text(lang.displayName, fontWeight = FontWeight.Bold) },
                                trailingContent = { Text(lang.nativeName, color = Color.Gray) },
                                modifier = Modifier.clickable {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("CANCEL") }
            }
        )
    }

    if (showEditDialog && userState != null) {
        EditProfileDialog(
            user = userState!!,
            onDismiss = { showEditDialog = false },
            onSave = { name, nickname, age, address, workplace ->
                viewModel.updateProfile(name, nickname, age, address, workplace, userState!!.profilePhotoPath)
                showEditDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Worker Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = safetyYellow,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Photo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (userState?.profilePhotoPath != null) {
                    AsyncImage(
                        model = userState!!.profilePhotoPath,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Gray
                    )
                }
            }
            
            Text(
                text = userState?.name?.uppercase() ?: "NOT SET",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 12.dp)
            )
            
            Text(
                text = "WORKER ID: ${userState?.workerId ?: "----"}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Today's Status
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("TODAY'S SAFETY DATA", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        val daysSafe = if (userState?.registrationTimestamp != null) {
                            ((System.currentTimeMillis() - userState!!.registrationTimestamp) / (1000 * 60 * 60 * 24)).toInt()
                        } else 0
                        
                        StatusItem("$daysSafe", "Days Safe", Color(0xFF2E7D32))
                        StatusItem("${userState?.dailySafetyScore ?: 0}", "Today's Pts", Color(0xFF1976D2))
                        StatusItem("${userState?.nearMisses ?: 0}", "Near Miss", Color(0xFFD32F2F))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safety Performance Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("OVERALL PERFORMANCE", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("WEEKLY SCORE", color = Color.White, fontSize = 12.sp)
                            Text("${userState?.weeklySafetyScore ?: 0}", color = safetyYellow, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TOTAL LIFETIME", color = Color.White, fontSize = 12.sp)
                            Text("${userState?.safetyScore ?: 0}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info Rows (Read-only display)
            // Information from registration is always displayed
            ProfileInfoRow("Full Name", userState?.name ?: "", Icons.Default.Badge, isRequired = true)
            ProfileInfoRow("Company Name", userState?.workplace ?: "", Icons.Default.Business, isRequired = true)
            ProfileInfoRow("Phone Number", userState?.phone ?: "", Icons.Default.Phone, isRequired = true)
            
            // Other information only if set
            if (!userState?.nickname.isNullOrBlank()) {
                ProfileInfoRow("Nickname", userState!!.nickname, Icons.Default.Face)
            }
            if (!userState?.age.isNullOrBlank()) {
                ProfileInfoRow("Age", userState!!.age, Icons.Default.CalendarToday)
            }
            if (!userState?.address.isNullOrBlank()) {
                ProfileInfoRow("Address", userState!!.address, Icons.Default.Home)
            }
            
            // Language Selection
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Text("App Language", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedCard(
                    onClick = { showLanguageDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Translate, null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(currentLang.displayName, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { showEditDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = safetyYellow)
                Spacer(modifier = Modifier.width(8.dp))
                Text("EDIT PROFILE", color = safetyYellow, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isRequired: Boolean = false) {
    if (value.isBlank() && !isRequired) return

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedCard(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = Color.Black)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (value.isBlank()) "Not set" else value,
                    fontWeight = FontWeight.Medium,
                    color = if (value.isBlank()) Color.Gray else Color.Black
                )
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    user: com.safety.rakshakavach.data.User,
    onDismiss: () -> Unit,
    onSave: (name: String, nickname: String, age: String, address: String, workplace: String) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var nickname by remember { mutableStateOf(user.nickname) }
    var workplace by remember { mutableStateOf(user.workplace) }
    var age by remember { mutableStateOf(user.age) }
    var address by remember { mutableStateOf(user.address) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile", fontWeight = FontWeight.Black) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = user.workerId,
                    onValueChange = {},
                    label = { Text("Worker ID") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Numbers, null) }
                )
                OutlinedTextField(
                    value = user.phone,
                    onValueChange = {},
                    label = { Text("Phone Number") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Phone, null) }
                )
                
                HorizontalDivider()

                // Editable fields
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Badge, null) }
                )
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Nickname") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Face, null) }
                )
                OutlinedTextField(
                    value = workplace,
                    onValueChange = { workplace = it },
                    label = { Text("Company Name") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Business, null) }
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) }
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Home, null) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, nickname, age, address, workplace) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("SAVE", color = Color(0xFFFFCC00))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color.Gray)
            }
        }
    )
}

@Composable
fun StatusItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 24.sp, fontWeight = FontWeight.Black)
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}
