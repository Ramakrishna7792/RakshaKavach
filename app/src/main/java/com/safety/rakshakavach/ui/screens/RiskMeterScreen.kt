package com.safety.rakshakavach.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import com.safety.rakshakavach.viewmodel.MainViewModel

data class WorkType(
    val id: String,
    val nameKey: String,
    val icon: ImageVector,
    val riskWithGear: Int,
    val riskWithoutGear: Int,
    val injuries: List<InjuryInfo>
)

data class InjuryInfo(
    val nameKey: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskMeterScreen(onBack: () -> Unit, viewModel: MainViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val getT = { key: String -> com.safety.rakshakavach.ui.Translations.getString(key, currentLang) }

    val safetyYellow = Color(0xFFFFCC00)
    var searchQuery by remember { mutableStateOf("") }
    var withGear by remember { mutableStateOf(true) }
    
    val workDatabase = remember {
        listOf(
            WorkType("welding", "welding", Icons.Default.Bolt, 20, 95, listOf(InjuryInfo("minor_burns", Icons.Default.Whatshot), InjuryInfo("eye_strain", Icons.Default.Visibility))),
            WorkType("height", "height_work", Icons.Default.Construction, 15, 98, listOf(InjuryInfo("serious_falls", Icons.Default.ArrowDownward))),
            WorkType("electrical", "electrical", Icons.Default.ElectricBolt, 10, 92, listOf(InjuryInfo("electric_shocks", Icons.Default.FlashOn))),
            WorkType("chemical", "chemical", Icons.Default.Science, 18, 85, listOf(InjuryInfo("chemical_burns", Icons.Default.BugReport))),
            WorkType("excavation", "excavation", Icons.Default.Tsunami, 30, 85, listOf(InjuryInfo("cave_ins", Icons.Default.Downloading))),
            WorkType("trench", "trench", Icons.Default.Agriculture, 28, 75, listOf(InjuryInfo("soil_collapse", Icons.Default.Terrain))),
            WorkType("confined", "confined", Icons.Default.MeetingRoom, 25, 90, listOf(InjuryInfo("asphyxiation", Icons.Default.Air))),
            WorkType("machinery", "machinery", Icons.Default.Settings, 22, 82, listOf(InjuryInfo("entanglement", Icons.Default.Loop))),
            WorkType("crane", "crane", Icons.Default.PrecisionManufacturing, 12, 88, listOf(InjuryInfo("falling_loads", Icons.Default.VerticalAlignBottom))),
            WorkType("forklift", "forklift", Icons.Default.LocalShipping, 15, 70, listOf(InjuryInfo("collisions", Icons.Default.Warning)))
        )
    }

    var selectedWork by remember { mutableStateOf(workDatabase[0]) }
    
    val filteredWork = workDatabase.filter { 
        getT(it.nameKey).contains(searchQuery, ignoreCase = true) 
    }

    val currentRisk = if (withGear) selectedWork.riskWithGear else selectedWork.riskWithoutGear
    val riskColor by animateColorAsState(
        targetValue = when {
            currentRisk >= 80 -> Color(0xFFD32F2F)
            currentRisk >= 60 -> Color(0xFFF57C00)
            currentRisk >= 40 -> Color(0xFFFBC02D)
            else -> Color(0xFF2E7D32)
        }, label = "riskColor"
    )
    val riskLabel = when {
        currentRisk >= 80 -> getT("critical_risk")
        currentRisk >= 60 -> getT("high_risk")
        currentRisk >= 40 -> getT("medium_risk")
        else -> getT("low_risk")
    }

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
            Text(getT("risk_meter"), fontSize = 24.sp, fontWeight = FontWeight.Black)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Search Section
            Text(
                "🔍 " + getT("search_work"),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(getT("search_placeholder").ifBlank { "Search for welding, electrical, roofing..." }) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.5f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.3f),
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                ),
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )

            if (searchQuery.isNotEmpty()) {
                Card(
                    modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        filteredWork.take(10).forEach { work ->
                            ListItem(
                                headlineContent = { Text(getT(work.nameKey), fontWeight = FontWeight.Bold) },
                                modifier = Modifier.clickable { 
                                    selectedWork = work
                                    searchQuery = ""
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Common Work Types Section
            Text(
                getT("common_work"),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                color = Color.Transparent,
                border = BorderStroke(2.dp, Color.Black.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                ) {
                    items(workDatabase) { work ->
                        val isSelected = selectedWork.id == work.id
                        Card(
                            onClick = { selectedWork = work },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(if (isSelected) 3.dp else 1.dp, if (isSelected) Color.Black else Color.Black.copy(alpha = 0.2f)),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color.Black else Color.White
                            ),
                            modifier = Modifier.height(90.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    getT(work.nameKey), 
                                    fontSize = 12.sp, 
                                    lineHeight = 14.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) safetyYellow else Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = if (work.riskWithoutGear >= 80) Color(0xFFD32F2F) else Color(0xFFF57C00),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        "${work.riskWithoutGear}% " + getT("risk"),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selection Display
            Surface(
                color = Color.Black,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(getT("viewing_stats"), color = Color(0xFFFFB74D), fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Text(getT(selectedWork.nameKey), color = safetyYellow, fontSize = 22.sp, fontWeight = FontWeight.Black, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Risk Controls
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(getT("view_risk_with"), fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { withGear = true },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (withGear) Color(0xFF2E7D32) else Color(0xFFE0E0E0)
                            )
                        ) {
                            Text("✓ " + getT("safety_gear"), color = if (withGear) Color.White else Color.Black, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { withGear = false },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!withGear) Color(0xFFD32F2F) else Color(0xFFE0E0E0)
                            )
                        ) {
                            Text("X " + getT("no_gear"), color = if (!withGear) Color.White else Color.Black, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Risk Meter Display
            Card(
                colors = CardDefaults.cardColors(containerColor = riskColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(180.dp),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(40.dp))
                    Text("${currentRisk}%", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text(riskLabel, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    val progress by animateFloatAsState(targetValue = currentRisk / 100f, label = "progress")
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Injuries Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Dangerous, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(getT("likely_injuries"), fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    selectedWork.injuries.forEach { injury ->
                        Surface(
                            color = Color(0xFFFFFDE7),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFFBC02D)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(injury.icon, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(getT(injury.nameKey), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Risk Comparison Card
            Surface(
                color = Color.Black,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(getT("risk_comparison"), color = safetyYellow, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ComparisonBox("${selectedWork.riskWithGear}%", getT("with_gear"), Color(0xFF2E7D32), Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        ComparisonBox("${selectedWork.riskWithoutGear}%", getT("without_gear"), Color(0xFFD32F2F), Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safety Warning Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFFFB74D), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(getT("responsibility_msg"), color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                    Text(getT("never_compromise"), color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ComparisonBox(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(80.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
