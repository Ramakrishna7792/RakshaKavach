package com.safety.rakshakavach.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safety.rakshakavach.viewmodel.MainViewModel

@Composable
fun TaskSelectionScreen(
    onTaskSelected: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: MainViewModel
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val getT = { key: String -> com.safety.rakshakavach.ui.Translations.getString(key, currentLang) }

    val safetyYellow = Color(0xFFFFCC00)
    val scrollState = rememberScrollState()

    val taskList = listOf(
        TaskInfo("Welding", "welding", "HIGH", Color(0xFFD32F2F), Icons.Default.Bolt),
        TaskInfo("Height Work", "height_work", "HIGH", Color(0xFFF57C00), Icons.Default.Construction),
        TaskInfo("Electrical", "electrical", "HIGH", Color(0xFFE65100), Icons.Default.ElectricBolt),
        TaskInfo("Chemical", "chemical", "HIGH", Color(0xFFC62828), Icons.Default.Science),
        TaskInfo("Excavation", "excavation", "HIGH", Color(0xFFBF360C), Icons.Default.Tsunami),
        TaskInfo("Trench", "trench", "MEDIUM", Color(0xFF8D6E63), Icons.Default.Agriculture),
        TaskInfo("Confined", "confined", "HIGH", Color(0xFF3E2723), Icons.Default.MeetingRoom),
        TaskInfo("Machinery", "machinery", "HIGH", Color(0xFF455A64), Icons.Default.Settings),
        TaskInfo("Crane", "crane", "HIGH", Color(0xFF263238), Icons.Default.PrecisionManufacturing),
        TaskInfo("Forklift", "forklift", "MEDIUM", Color(0xFF546E7A), Icons.Default.LocalShipping)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(safetyYellow)
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = getT("back"),
                    tint = safetyYellow
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                getT("select_task"),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }

        // Info Banner
        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFFB74D),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    getT("select_task_msg"),
                    color = safetyYellow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Task List
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            taskList.forEach { task ->
                TaskCard(
                    title = getT(task.nameKey),
                    risk = getT(task.risk.lowercase() + "_risk").ifBlank { task.risk },
                    color = task.color,
                    icon = task.icon,
                    onClick = { onTaskSelected(task.id) },
                    tapToViewText = getT("tap_to_view")
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class TaskInfo(
    val id: String,
    val nameKey: String,
    val risk: String,
    val color: Color,
    val icon: ImageVector
)

@Composable
fun TaskCard(
    title: String,
    risk: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    tapToViewText: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
            .height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 22.sp
                )
                Text(
                    text = tapToViewText,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Surface(
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(50),
            ) {
                Text(
                    text = risk,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
