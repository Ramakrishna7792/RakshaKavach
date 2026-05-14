package com.safety.rakshakavach.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.safety.rakshakavach.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope

@Composable
fun ChecklistScreen(
    taskType: String, 
    onBack: () -> Unit, 
    onStartWork: () -> Unit,
    viewModel: MainViewModel
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val getT = { key: String -> com.safety.rakshakavach.ui.Translations.getString(key, currentLang) }

    val safetyYellow = Color(0xFFFFCC00)
    val scrollState = rememberScrollState()
    val activeTask by viewModel.activeTask.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingPpe.collectAsState()
    val scope = rememberCoroutineScope()
    
    val isWorkInProgress = activeTask == taskType
    var capturedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    val ppeItems = when (taskType) {
        "Welding" -> listOf(
            PpeItem("Safety Helmet", "Protects head from falling objects", Icons.Default.Engineering),
            PpeItem("Safety Gloves", "Protects hands from cuts & burns", Icons.Default.PanTool),
            PpeItem("Safety Boots", "Steel toe protection", Icons.Default.IceSkating),
            PpeItem("Safety Goggles", "Protects eyes from debris & sparks", Icons.Default.Visibility),
            PpeItem("Safety Jacket", "Fire resistant clothing", Icons.Default.Checkroom)
        )
        "Height Work" -> listOf(
            PpeItem("Safety Harness", "Fall arrest system", Icons.Default.Accessibility),
            PpeItem("Safety Helmet", "Protects head from falling objects", Icons.Default.Engineering),
            PpeItem("Safety Boots", "Anti-slip steel toe boots", Icons.Default.IceSkating),
            PpeItem("Safety Gloves", "Grip protection", Icons.Default.PanTool)
        )
        "Digging Trench" -> listOf(
            PpeItem("Safety Helmet", "Protects head", Icons.Default.Engineering),
            PpeItem("Hi-Viz Vest", "High visibility clothing", Icons.Default.Checkroom),
            PpeItem("Safety Boots", "Heavy duty protection", Icons.Default.IceSkating),
            PpeItem("Gloves", "General hand protection", Icons.Default.PanTool)
        )
        else -> listOf(
            PpeItem("Safety Helmet", "Head protection", Icons.Default.Engineering),
            PpeItem("Safety Gloves", "Hand protection", Icons.Default.PanTool),
            PpeItem("Safety Boots", "Foot protection", Icons.Default.IceSkating)
        )
    }
    
    val checkedItems = remember { 
        mutableStateListOf<String>().apply {
            if (isWorkInProgress) {
                addAll(ppeItems.map { it.name })
            }
        }
    }
    val isComplete = checkedItems.size == ppeItems.size

    val context = androidx.compose.ui.platform.LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            scope.launch {
                val found = viewModel.analyzePpeWithGemini(bitmap)
                if (found.isEmpty()) {
                    android.widget.Toast.makeText(context, "No safety gear detected. Try a clearer photo.", android.widget.Toast.LENGTH_LONG).show()
                } else {
                    android.widget.Toast.makeText(context, "Detected: ${found.joinToString(", ")}", android.widget.Toast.LENGTH_SHORT).show()
                    found.forEach { item ->
                        val matchedItem = ppeItems.find { it.name.equals(item, ignoreCase = true) }
                        if (matchedItem != null && !checkedItems.contains(matchedItem.name)) {
                            checkedItems.add(matchedItem.name)
                        }
                    }
                }
            }
        }
    }

    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            showPermissionDialog = true
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(getT("permission_needed").ifBlank { "Camera Permission Needed" }, fontWeight = FontWeight.Black) },
            text = { Text("To scan your safety gear using AI, we need access to your camera. Please enable it in app settings or grant permission.") },
            confirmButton = {
                Button(onClick = { 
                    showPermissionDialog = false
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }) {
                    Text("TRY AGAIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("CANCEL")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(safetyYellow)
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(Color.Black, RoundedCornerShape(12.dp)).size(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = safetyYellow)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(getT("safety_gear_checklist"), fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp).align(Alignment.CenterHorizontally)
        ) {
            Text("${getT("select_task")}: ${taskType.uppercase()} WORK", color = safetyYellow, fontSize = 12.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scanned Image Display
        if (capturedBitmap != null || isAnalyzing) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(200.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (capturedBitmap != null) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Scanned Gear",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    if (isAnalyzing) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = safetyYellow)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("AI ANALYZING GEAR...", color = Color.White, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // AI Scan Button
        if (!isWorkInProgress) {
            Button(
                onClick = { 
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                enabled = !isAnalyzing
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(getT("ai_scan"), fontWeight = FontWeight.Black)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(getT("completion_status"), fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Surface(color = if (isComplete) Color(0xFF2E7D32) else Color(0xFFD1D9E6), shape = RoundedCornerShape(12.dp)) {
                        Text("${checkedItems.size}/${ppeItems.size}", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.Black, fontSize = 20.sp, color = if (isComplete) Color.White else Color.Black)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { checkedItems.size.toFloat() / ppeItems.size },
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                    color = Color(0xFF2E7D32),
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Warning/Success Banner
        Surface(
            color = if (isComplete) Color(0xFF1B5E20) else Color(0xFFE65100),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(if (isComplete) "✓ ${getT("all_verified")}" else getT("incomplete_checklist"), color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text(if (isComplete) getT("ready_msg") else getT("wear_all_msg"), color = Color.White, fontSize = 12.sp)
                    }
                }
                
                if (isComplete) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ProtectedWorkerDrawing()
                }
            }
        }

        // Checklist Items
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ppeItems.forEach { item ->
                PpeCheckItem(
                    item = item,
                    isChecked = checkedItems.contains(item.name),
                    // MODE TOGGLE: Set isEnabled to 'false' to force AI Scan Only
                    isEnabled = !isWorkInProgress, 
                    onCheckedChange = { isChecked ->
                        // MODE TOGGLE: Comment out this block to force AI Scan Only
                        if (isChecked) {
                            if (!checkedItems.contains(item.name)) checkedItems.add(item.name)
                        } else {
                            checkedItems.remove(item.name)
                        }
                    }
                )
            }
        }

        // Bottom Button
        Button(
            onClick = {
                if (isWorkInProgress) viewModel.finishActiveTask() else viewModel.completeChecklist(taskType)
                onStartWork()
            },
            enabled = isComplete || isWorkInProgress,
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(70.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isWorkInProgress) Color.Red else Color.Black)
        ) {
            Text(if (isWorkInProgress) getT("finish_work") else getT("start_work"), fontWeight = FontWeight.Black, fontSize = 20.sp, color = if (isWorkInProgress) Color.White else safetyYellow)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProtectedWorkerDrawing() {
    Surface(
        color = Color(0xFF388E3C),
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .clip(RoundedCornerShape(16.dp)),
        border = androidx.compose.foundation.BorderStroke(3.dp, Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                "PROTECTED WORKER", 
                color = Color.White, 
                fontWeight = FontWeight.Black, 
                fontSize = 16.sp,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawDetailedWorker()
            }
        }
    }
}

fun DrawScope.drawDetailedWorker() {
    val scale = size.height / 300f
    val centerX = size.width / 2
    
    // Proportions
    val headRadius = 35f * scale
    val headY = 80f * scale
    val bodyWidth = 100f * scale
    val bodyHeight = 110f * scale
    val bodyY = headY + headRadius + 10f * scale
    
    // 1. Legs (Blue Trousers)
    val legWidth = 40f * scale
    val legHeight = 80f * scale
    val legY = bodyY + bodyHeight - 10f * scale
    // Left Leg
    drawRoundRect(color = Color(0xFF1976D2), topLeft = Offset(centerX - legWidth - 2f * scale, legY), size = Size(legWidth, legHeight), cornerRadius = CornerRadius(10f * scale))
    // Right Leg
    drawRoundRect(color = Color(0xFF1976D2), topLeft = Offset(centerX + 2f * scale, legY), size = Size(legWidth, legHeight), cornerRadius = CornerRadius(10f * scale))

    // 2. Boots (Black)
    val bootWidth = 45f * scale
    val bootHeight = 25f * scale
    val bootY = legY + legHeight - 5f * scale
    drawRoundRect(color = Color(0xFF212121), topLeft = Offset(centerX - bootWidth - 5f * scale, bootY), size = Size(bootWidth, bootHeight), cornerRadius = CornerRadius(8f * scale))
    drawRoundRect(color = Color(0xFF212121), topLeft = Offset(centerX + 5f * scale, bootY), size = Size(bootWidth, bootHeight), cornerRadius = CornerRadius(8f * scale))

    // 3. Body (Orange Safety Vest)
    drawRoundRect(
        color = Color(0xFFFF9800),
        topLeft = Offset(centerX - bodyWidth/2, bodyY),
        size = Size(bodyWidth, bodyHeight),
        cornerRadius = CornerRadius(15f * scale)
    )
    // Vest detail (reflective strips)
    drawRect(color = Color(0xFFEEEEEE), topLeft = Offset(centerX - bodyWidth/2, bodyY + 30f * scale), size = Size(bodyWidth, 15f * scale))
    drawRect(color = Color(0xFFEEEEEE), topLeft = Offset(centerX - bodyWidth/2, bodyY + 70f * scale), size = Size(bodyWidth, 15f * scale))

    // 4. Arms
    val armWidth = 25f * scale
    val armHeight = 90f * scale
    // Left Arm
    drawRoundRect(color = Color(0xFFFFD54F), topLeft = Offset(centerX - bodyWidth/2 - armWidth + 5f * scale, bodyY + 5f * scale), size = Size(armWidth, armHeight), cornerRadius = CornerRadius(10f * scale))
    // Right Arm
    drawRoundRect(color = Color(0xFFFFD54F), topLeft = Offset(centerX + bodyWidth/2 - 5f * scale, bodyY + 5f * scale), size = Size(armWidth, armHeight), cornerRadius = CornerRadius(10f * scale))

    // 5. Gloves (Red)
    val gloveSize = 35f * scale
    drawRoundRect(color = Color(0xFFD32F2F), topLeft = Offset(centerX - bodyWidth/2 - armWidth, bodyY + armHeight - 15f * scale), size = Size(gloveSize, gloveSize), cornerRadius = CornerRadius(8f * scale))
    drawRoundRect(color = Color(0xFFD32F2F), topLeft = Offset(centerX + bodyWidth/2 - 5f * scale, bodyY + armHeight - 15f * scale), size = Size(gloveSize, gloveSize), cornerRadius = CornerRadius(8f * scale))

    // 6. Head
    drawCircle(color = Color(0xFFFFD54F), radius = headRadius, center = Offset(centerX, headY))
    
    // 7. Helmet (Yellow)
    drawArc(
        color = Color(0xFFFFEB3B),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        size = Size(headRadius * 2.8f, headRadius * 2f),
        topLeft = Offset(centerX - headRadius * 1.4f, headY - headRadius * 1.1f)
    )
    // Helmet Brim
    drawRoundRect(
        color = Color(0xFFFFEB3B),
        topLeft = Offset(centerX - headRadius * 1.5f, headY - 10f * scale),
        size = Size(headRadius * 3f, 12f * scale),
        cornerRadius = CornerRadius(5f * scale)
    )

    // 8. Face details (Eyes/Visor)
    drawRoundRect(
        color = Color(0xFF333333),
        topLeft = Offset(centerX - 20f * scale, headY - 5f * scale),
        size = Size(40f * scale, 12f * scale),
        cornerRadius = CornerRadius(4f * scale)
    )
}

@Composable
fun PpeCheckItem(item: PpeItem, isChecked: Boolean, isEnabled: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isChecked) Color(0xFF2E7D32) else Color.White),
        modifier = Modifier.fillMaxWidth().clickable(enabled = isEnabled) { onCheckedChange(!isChecked) },
        border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(item.icon, null, modifier = Modifier.size(32.dp), tint = if (isChecked) Color.White else Color.Black)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Black, fontSize = 18.sp, color = if (isChecked) Color.White else Color.Black)
                Text(item.description, fontSize = 12.sp, color = if (isChecked) Color.White.copy(alpha = 0.8f) else Color.Gray)
            }
            if (isChecked) Icon(Icons.Default.Check, null, tint = Color.White)
        }
    }
}

data class PpeItem(val name: String, val description: String, val icon: ImageVector)
