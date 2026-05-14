package com.safety.rakshakavach.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safety.rakshakavach.viewmodel.MainViewModel

@Composable
fun LeaderboardScreen(onBack: () -> Unit, viewModel: MainViewModel) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val getT = { key: String -> com.safety.rakshakavach.ui.Translations.getString(key, currentLang) }

    val safetyYellow = Color(0xFFFFCC00)
    
    LaunchedEffect(Unit) {
        viewModel.fetchLeaderboard()
    }

    Column(modifier = Modifier.fillMaxSize().background(safetyYellow)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(Color.Black, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, getT("back"), tint = safetyYellow)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("WEEKLY CHAMPIONS", fontSize = 24.sp, fontWeight = FontWeight.Black)
        }

        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFD700), modifier = Modifier.size(80.dp))
                    }
                }

                itemsIndexed(leaderboard) { index, user ->
                    val rankColor = when(index) {
                        0 -> Color(0xFFFFD700) // Gold
                        1 -> Color(0xFFC0C0C0) // Silver
                        2 -> Color(0xFFCD7F32) // Bronze
                        else -> Color.Black
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = if (index < 3) rankColor.copy(alpha = 0.1f) else Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("#${index + 1}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = rankColor, modifier = Modifier.width(40.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${getT("worker_id").split(" ")[0]} #${user.workerId}", fontWeight = FontWeight.Bold)
                                Text(user.workplace, fontSize = 12.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${user.weeklySafetyScore}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF2E7D32))
                                Text("WEEKLY PTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
