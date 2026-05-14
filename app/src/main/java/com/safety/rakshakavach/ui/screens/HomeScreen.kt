package com.safety.rakshakavach.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.safety.rakshakavach.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel) {
    val userState by viewModel.currentUser.collectAsState()
    val activeTask by viewModel.activeTask.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val sosStatus by viewModel.registrationError.collectAsState()
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val safetyYellow = Color(0xFFFFCC00)

    val getT = { key: String -> com.safety.rakshakavach.ui.Translations.getString(key, currentLang) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.SEND_SMS] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        
        if (smsGranted) {
            viewModel.sendSos(if (locationGranted) "Fetching GPS..." else "Floor 3, Zone B (Simulated)")
        } else {
            viewModel.clearErrors() // Clear any old ones
            // We can't easily set a new error without a dedicated method, 
            // but we can trigger a snackbar manually or just let the user know.
        }
    }

    var currentTime by remember { mutableStateOf(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())) }
    var currentDate by remember { mutableStateOf(SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()).format(Date())) }

    LaunchedEffect(sosStatus) {
        sosStatus?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrors()
        }
    }

    LaunchedEffect(Unit) {
        while(true) {
            currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            currentDate = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()).format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = safetyYellow
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Time & Date Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(currentDate, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(currentTime, color = Color.Black, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            // Header Section
            Surface(
                color = Color.Black,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = getT("app_name").split(" ")[0],
                            color = safetyYellow,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = getT("app_name").split(" ").getOrElse(1) { "" },
                            color = safetyYellow,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 18.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(getT("welcome"), color = safetyYellow, fontSize = 12.sp)
                            Text(
                                userState?.name ?: "WORKER #${userState?.workerId ?: "----"}",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .background(Color.DarkGray)
                                .clickable { navController.navigate("profile") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (userState?.profilePhotoPath != null) {
                                AsyncImage(
                                    model = userState?.profilePhotoPath,
                                    contentDescription = "Profile",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = safetyYellow,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safety Score Cards (Daily & Weekly)
            Row(modifier = Modifier.fillMaxWidth()) {
                ScoreCard(
                    title = "DAILY SCORE",
                    score = userState?.dailySafetyScore ?: 0,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                ScoreCard(
                    title = "WEEKLY SCORE",
                    score = userState?.weeklySafetyScore ?: 0,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Leaderboard Access
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().clickable { navController.navigate("leaderboard") }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFD700), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("VIEW WEEKLY LEADERBOARD", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text("See who are the top safety champions this week", fontSize = 11.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Operation or Safety Reminder
            if (activeTask != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("checklist/$activeTask") }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(30.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(getT("active_op"), color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "$activeTask WORK",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                            Text("Tap to view checklist or finish shift", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WarningAmber, null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "START-OF-DAY SAFETY REMINDER",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Before starting work, ensure you have ALL required safety equipment. Your life depends on it!",
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SOS Button
            Surface(
                color = Color(0xFFD32F2F),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(
                            onClick = { 
                                // Show a hint on simple tap
                                scope.launch { snackbarHostState.showSnackbar("LONG PRESS to trigger Emergency SOS") }
                            },
                            onLongClick = {
                                val smsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                                val locPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                                
                                if (smsPermission == PackageManager.PERMISSION_GRANTED) {
                                    viewModel.sendSos(if (locPermission == PackageManager.PERMISSION_GRANTED) "Floor 3, Zone B" else "Location access denied")
                                } else {
                                    permissionLauncher.launch(arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION))
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Emergency, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("EMERGENCY SOS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Text("HOLD TO SEND LOCATION ALERT", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid Section
            Row(modifier = Modifier.fillMaxWidth()) {
                GridItem(
                    title = if (activeTask != null) "OPERATION ACTIVE" else getT("select_task"),
                    icon = if (activeTask != null) Icons.Default.LockClock else Icons.AutoMirrored.Filled.Assignment,
                    containerColor = Color.Black,
                    contentColor = safetyYellow,
                    modifier = Modifier.weight(1f),
                    onClick = { if (activeTask == null) navController.navigate("task_selection") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                GridItem(
                    title = getT("report_incident"),
                    icon = Icons.Default.Warning,
                    containerColor = Color(0xFFF57C00),
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("incidents") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                GridItem(
                    title = getT("risk_meter"),
                    icon = Icons.Default.ReportProblem,
                    containerColor = Color(0xFFD32F2F),
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("risk_meter") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                GridItem(
                    title = getT("daily_quiz"),
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    containerColor = Color(0xFF1976D2),
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("quiz") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ScoreCard(title: String, score: Int, color: Color, modifier: Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.Gray)
            Text("$score", fontWeight = FontWeight.Black, fontSize = 32.sp, color = color)
            Text("POINTS", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun GridItem(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                color = contentColor,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
