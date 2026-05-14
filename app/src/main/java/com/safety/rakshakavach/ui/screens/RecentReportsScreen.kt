package com.safety.rakshakavach.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safety.rakshakavach.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RecentReportsScreen(onBack: () -> Unit, viewModel: MainViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val getT = { key: String -> com.safety.rakshakavach.ui.Translations.getString(key, currentLang) }

    val safetyYellow = Color(0xFFFFCC00)
    val incidents by viewModel.incidents.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(safetyYellow)
    ) {
        // Header
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .size(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, getT("back"), tint = safetyYellow)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(getT("your_reports"), fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            
            val context = androidx.compose.ui.platform.LocalContext.current
            IconButton(
                onClick = { viewModel.exportIncidentsToPdf(context) },
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .size(48.dp)
            ) {
                Icon(Icons.Default.Description, getT("export_pdf"), tint = safetyYellow)
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    getT("recent_reports"),
                    color = Color.White, 
                    fontWeight = FontWeight.Black, 
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
                )

                if (incidents.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(getT("no_reports"), color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(incidents) { incident ->
                            ReportItemDetailed(incident, safetyYellow, getT)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ReportItemDetailed(incident: com.safety.rakshakavach.data.Incident, cardColor: Color, getT: (String) -> String) {
    val date = SimpleDateFormat("MMMM d, yyyy HH:mm", Locale.getDefault()).format(Date(incident.timestamp))
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(getT(incident.type), fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.Black)
                Surface(
                    color = when(incident.severity) {
                        "HIGH" -> Color(0xFFD32F2F)
                        "MEDIUM" -> Color(0xFFC67100)
                        else -> Color(0xFF2E7D32)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f))
                ) {
                    Text(
                        getT(incident.severity.lowercase()).ifBlank { incident.severity },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(incident.location, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                color = Color.Black.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    incident.description, 
                    fontSize = 14.sp, 
                    color = Color.Black,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(14.dp), tint = Color.Black.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.width(4.dp))
                Text(date, fontSize = 11.sp, color = Color.Black.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
            }

            if (incident.witnesses.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("${getT("witnesses_label")} ${incident.witnesses}", fontSize = 11.sp, color = Color.Black.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
            }
        }
    }
}
