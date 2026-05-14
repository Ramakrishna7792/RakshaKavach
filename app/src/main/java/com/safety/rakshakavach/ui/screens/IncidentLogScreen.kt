package com.safety.rakshakavach.ui.screens

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safety.rakshakavach.viewmodel.MainViewModel
import com.safety.rakshakavach.data.Incident
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentLogScreen(onBack: () -> Unit, onNavigateToReports: () -> Unit, viewModel: MainViewModel) {
    var location by remember { mutableStateOf("") }
    var incidentType by remember { mutableStateOf("Select type...") }
    var otherIncidentType by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("MEDIUM") }
    var description by remember { mutableStateOf("") }
    var witnesses by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())) }
    
    val currentLang by viewModel.currentLanguage.collectAsState()
    val getT = { key: String -> com.safety.rakshakavach.ui.Translations.getString(key, currentLang) }

    val selectTypeText = getT("select_type")
    LaunchedEffect(currentLang) {
        if (incidentType == "Select type..." || incidentType == "प्रकार चुनें...") {
            incidentType = selectTypeText
        }
    }

    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showPermissionDialog = true
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(getT("permission_needed").ifBlank { "Permission Needed" }, fontWeight = FontWeight.Black) },
            text = { Text("This feature requires permission to work properly. Please grant the permission in settings.") },
            confirmButton = {
                Button(onClick = { showPermissionDialog = false }) { Text("OK") }
            }
        )
    }

    val safetyYellow = Color(0xFFFFCC00)
    var expandedType by remember { mutableStateOf(false) }
    val types = listOf("near_miss", "injury", "unsafe_condition", "falling_object", "equipment_failure", "other")

    // Voice to Text Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        if (!data.isNullOrEmpty()) {
            description = data[0]
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(safetyYellow).verticalScroll(rememberScrollState())) {
        // ... (Header and Banner)
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
            Text(getT("incident_log"), fontSize = 24.sp, fontWeight = FontWeight.Black)
        }

        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Alert Banner
                Surface(
                    color = Color(0xFFFF5722),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(getT("report_near_miss"), color = Color.White, fontWeight = FontWeight.Black)
                            Text(getT("reporting_msg"), color = Color.White, fontSize = 11.sp, lineHeight = 14.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Date & Time
                FieldLabel(getT("date_time"), Icons.Default.Event)
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Location Field
                FieldLabel(getT("location"), Icons.Default.LocationOn)
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("e.g., Building 3, Floor 2") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                            // In a real app, we'd trigger location fetch here
                            location = "Detecting location..." 
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                location = "Industrial Area, Zone 4" // Mocked location
                            }, 1500)
                        }) {
                            Icon(Icons.Default.MyLocation, "Detect Location", tint = Color(0xFF1976D2))
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Incident Type Dropdown
                FieldLabel(getT("incident_type"), Icons.Default.ErrorOutline)
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (incidentType == selectTypeText) selectTypeText else getT(incidentType),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Black)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        types.forEach { typeKey ->
                            DropdownMenuItem(
                                text = { Text(getT(typeKey)) },
                                onClick = {
                                    incidentType = typeKey
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                if (incidentType == "other") {
                    Spacer(modifier = Modifier.height(16.dp))
                    FieldLabel(getT("specify_other"), Icons.Default.Edit)
                    OutlinedTextField(
                        value = otherIncidentType,
                        onValueChange = { otherIncidentType = it },
                        placeholder = { Text(getT("specify_other")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Black)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Severity Selector
                Text(getT("severity_level"), fontWeight = FontWeight.Black, fontSize = 13.sp)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    SeverityButton(getT("low"), Color(0xFF2E7D32), severity == "LOW") { severity = "LOW" }
                    Spacer(modifier = Modifier.width(8.dp))
                    SeverityButton(getT("medium"), Color(0xFFFFA000), severity == "MEDIUM") { severity = "MEDIUM" }
                    Spacer(modifier = Modifier.width(8.dp))
                    SeverityButton(getT("high"), Color(0xFFD32F2F), severity == "HIGH") { severity = "HIGH" }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description with Voice support
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FieldLabel(getT("description"), Icons.Default.Description)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe the incident...")
                        }
                        speechLauncher.launch(intent)
                    }) {
                        Icon(Icons.Default.Mic, "Voice Report", tint = Color(0xFF1976D2))
                    }
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Describe what happened, what could have happened, and any immediate actions taken...") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Witnesses Field
                FieldLabel(getT("witnesses"), Icons.Default.People)
                OutlinedTextField(
                    value = witnesses,
                    onValueChange = { witnesses = it },
                    placeholder = { Text("Names of any witnesses") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Black)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val finalType = if (incidentType == "other") otherIncidentType else getT(incidentType)
                        viewModel.reportIncident(finalType, location, severity, description, witnesses)
                        location = ""; description = ""; witnesses = ""; incidentType = selectTypeText; otherIncidentType = ""
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = (location.isNotBlank() && description.isNotBlank() && incidentType != selectTypeText && (incidentType != "other" || otherIncidentType.isNotBlank())),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(getT("submit_report"), fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }
        }

        // View Reports Navigation Card
        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { onNavigateToReports() },
            color = Color.Black,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(getT("your_reports"), color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(getT("view_reports_msg"), color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = safetyYellow)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun FieldLabel(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
    }
}

@Composable
fun RowScope.SeverityButton(label: String, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.weight(1f).height(40.dp).clickable { onClick() },
        color = if (isSelected) color else Color.LightGray.copy(alpha = 0.3f),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
