package com.safety.rakshakavach.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safety.rakshakavach.viewmodel.MainViewModel
import java.util.*

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

@Composable
fun QuizScreen(onBack: () -> Unit, viewModel: MainViewModel) {
    val userState by viewModel.currentUser.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val getT = { key: String -> com.safety.rakshakavach.ui.Translations.getString(key, currentLang) }
    val safetyYellow = Color(0xFFFFCC00)

    val isQuizAvailable = remember(userState?.lastQuizTimestamp) {
        val lastQuiz = userState?.lastQuizTimestamp ?: 0L
        val lastQuizCal = Calendar.getInstance().apply { timeInMillis = lastQuiz }
        val nowCal = Calendar.getInstance()
        
        lastQuiz == 0L || (nowCal.get(Calendar.DAY_OF_YEAR) != lastQuizCal.get(Calendar.DAY_OF_YEAR) || 
                          nowCal.get(Calendar.YEAR) != lastQuizCal.get(Calendar.YEAR))
    }

    if (!isQuizAvailable) {
        Column(
            modifier = Modifier.fillMaxSize().background(safetyYellow),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.LockClock, null, modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text("QUIZ TAKEN TODAY", fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text("The daily safety quiz resets at midnight.", textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp))
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                Text("GO BACK", color = safetyYellow)
            }
        }
        return
    }
    
    val questionPool = remember {
        listOf(
            Question(1, "What should you do BEFORE starting welding work?", listOf("Start immediately", "Check safety equipment & ventilation", "Just wear goggles", "Ask someone else"), 1, "Always check ALL safety equipment and ensure proper ventilation before welding."),
            Question(2, "If you see a co-worker without a helmet in a construction zone, you should:", listOf("Ignore it", "Report to get them in trouble", "Remind them & report unsafe condition", "Wait until someone gets hurt"), 2, "Safety is everyone's responsibility. Speak up immediately to prevent accidents."),
            Question(3, "What is the primary purpose of a 'Near Miss' report?", listOf("To blame others", "To prevent future accidents", "To waste time", "To get a bonus"), 1, "Near miss reporting identifies hazards before they cause injuries, making the workplace safer for everyone."),
            Question(4, "Which PPE is essential when handling hazardous chemicals?", listOf("Sunglasses", "Leather apron", "Chemical-resistant gloves & face shield", "T-shirt"), 2, "Proper skin and eye protection are critical to prevent chemical burns and irritation."),
            Question(5, "When working at heights, when is a safety harness required?", listOf("Only when you feel dizzy", "Above 6 feet (1.8 meters)", "Only if the boss is watching", "Never"), 1, "Fall protection is mandatory for work at heights to prevent fatal injuries."),
            Question(6, "What does a 'Lockout/Tagout' procedure prevent?", listOf("Lunch breaks", "Accidental machine startup", "Entry to the building", "Tool theft"), 1, "LOTO ensures that machines remain powered off during maintenance to protect workers."),
            Question(7, "If a fire alarm sounds, you should:", listOf("Finish your task", "Run as fast as possible", "Follow evacuation routes calmly", "Hide under a desk"), 2, "Following designated emergency routes ensures a safe and organized evacuation."),
            Question(8, "Safety boots with steel toes protect against:", listOf("Wet feet", "Cold weather", "Falling heavy objects & punctures", "Fast running"), 2, "Steel toes are designed to prevent crushing injuries from heavy equipment or materials."),
            Question(9, "What should you do if you find a damaged power tool?", listOf("Tape it up", "Use it carefully", "Tag it 'Out of Service' & report it", "Give it to a trainee"), 2, "Damaged tools are electrical and mechanical hazards; they must be removed from use immediately."),
            Question(10, "Why is proper manual lifting technique important?", listOf("To look professional", "To lift heavier weights", "To prevent back and muscle strain", "To finish faster"), 2, "Lifting with your legs instead of your back prevents long-term musculoskeletal injuries."),
            Question(11, "Which of these is a symptom of heat exhaustion?", listOf("Increased energy", "Dizziness and heavy sweating", "Cold, dry skin", "Feeling very hungry"), 1, "Recognizing heat exhaustion early can prevent life-threatening heat stroke."),
            Question(12, "In a 'Confined Space', what is the biggest invisible danger?", listOf("Darkness", "Lack of space", "Harmful gases or low oxygen", "Noise"), 2, "Atmospheric hazards in confined spaces can be fatal without proper testing and ventilation."),
            Question(13, "What is the correct way to use a portable fire extinguisher?", listOf("Aim at the flames", "PASS: Pull, Aim, Squeeze, Sweep", "Pour water first", "Shake it vigorously"), 1, "The PASS method is the industry standard for effectively using a fire extinguisher."),
            Question(14, "Ear protection (plugs/muffs) should be worn when noise exceeds:", listOf("50 decibels", "85 decibels", "120 decibels", "Only if it hurts"), 1, "Prolonged exposure to noise above 85dB can cause permanent hearing loss."),
            Question(15, "What should you do if you spill a small amount of unknown chemical?", listOf("Wipe it with a paper towel", "Wash it with water", "Check the SDS (Safety Data Sheet)", "Ignore it"), 2, "The SDS provides critical info on how to safely handle and clean up specific chemicals."),
            Question(16, "A ladder should be placed at what angle against a wall?", listOf("1:1 ratio", "4:1 ratio (75 degrees)", "Flat as possible", "90 degrees"), 1, "The 4-to-1 rule ensures ladder stability and prevents it from slipping or tipping."),
            Question(17, "When using a grinder, you must always have:", listOf("Music playing", "A handle removed", "A safety guard in place", "Gloves only"), 2, "Safety guards prevent contact with the wheel and protect from flying debris or wheel breakage."),
            Question(18, "The 'Three Point Contact' rule applies to:", listOf("Welding", "Climbing ladders or equipment", "First Aid", "Driving a forklift"), 1, "Maintaining three points of contact (2 hands, 1 foot or 1 hand, 2 feet) prevents falls."),
            Question(19, "Who is responsible for inspecting safety gear before use?", listOf("The Safety Officer", "The Supervisor", "The Worker using it", "The Manufacturer"), 2, "Every worker must inspect their own PPE before each use to ensure it is in safe condition."),
            Question(20, "What color is a standard 'Caution' sign?", listOf("Red", "Blue", "Yellow", "Green"), 2, "Yellow signs indicate caution and potential hazards that require attention."),
            Question(21, "If a machine guard is missing, you should:", listOf("Work carefully", "Wait for the end of the shift", "Report it and don't use the machine", "Make a temporary guard"), 2, "Never operate machinery with missing or bypassed safety guards."),
            Question(22, "What is the first step in treating a minor burn?", listOf("Apply ice directly", "Apply butter", "Run cool water over it", "Pop any blisters"), 2, "Cooling the burn with running water is the most effective immediate treatment."),
            Question(23, "When driving a forklift, the load should be:", listOf("As high as possible", "At eye level", "As low as possible", "Tilted forward"), 2, "Keeping loads low improves visibility and prevents the forklift from tipping over."),
            Question(24, "What does 'PPE' stand for?", listOf("Permanent Plant Equipment", "Personal Protective Equipment", "Private Protection Entry", "Public Power Energy"), 1, "PPE includes all gear designed to protect workers from serious workplace injuries."),
            Question(25, "Safety 'Near Miss' reports should be filed:", listOf("Only for serious incidents", "As soon as possible", "At the end of the month", "Only if requested"), 1, "Reporting immediately ensures that hazards are addressed before someone gets hurt."),
            Question(26, "Scaffolding must be inspected by a 'Competent Person':", listOf("Once a year", "Every day before use", "Only after a fall", "Once a month"), 1, "Daily inspections ensure the structure remains stable and safe for workers."),
            Question(27, "When working with electricity, you should assume:", listOf("The power is off", "The wire is insulated", "The circuit is live", "Gloves aren't needed"), 2, "Always treat electrical circuits as live until they are tested and proven dead."),
            Question(28, "A 'Safety Data Sheet' (SDS) must be accessible to:", listOf("Management only", "Emergency services", "All workers using the chemicals", "The Safety Inspector"), 2, "Workers have a right to know the hazards of the materials they work with."),
            Question(29, "What should you do if you feel too tired to work safely?", listOf("Drink more coffee", "Push through it", "Inform your supervisor", "Work faster to finish"), 2, "Fatigue is a major cause of workplace accidents; it must be managed correctly."),
            Question(30, "What is the purpose of a 'Toolbox Talk'?", listOf("To fix broken tools", "To discuss safety before a shift", "To complain about work", "To distribute paychecks"), 1, "Toolbox talks are short meetings to discuss specific safety concerns for the day's tasks."),
            Question(31, "The best way to prevent trips is:", listOf("Good housekeeping", "Warning signs only", "Walking slowly", "Wearing bright colors"), 0, "Keeping work areas clear of clutter and debris is the most effective way to prevent trips.")
        )
    }

    val questions = remember { questionPool.shuffled().take(5) }
    
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }

    val currentQuestion = questions[currentQuestionIndex]
    val isCorrect = selectedOptionIndex == currentQuestion.correctAnswerIndex

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
                    .size(40.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, getT("back"), tint = safetyYellow, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(getT("daily_quiz_title"), fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${getT("question")} ${currentQuestionIndex + 1} ${getT("of")} ${questions.size}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${getT("score_label")} $score", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = Color.Blue,
                        trackColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center, 
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = currentQuestion.text,
                        color = safetyYellow,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = selectedOptionIndex == index
                    val optionBgColor = when {
                        !isAnswered && isSelected -> Color.LightGray
                        isAnswered && index == currentQuestion.correctAnswerIndex -> Color(0xFF2E7D32)
                        isAnswered && isSelected -> Color(0xFFD32F2F)
                        else -> Color.White
                    }
                    val contentColor = if (isAnswered && (isSelected || index == currentQuestion.correctAnswerIndex)) Color.White else Color.Black

                    Card(
                        colors = CardDefaults.cardColors(containerColor = optionBgColor),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clickable(enabled = !isAnswered) {
                                selectedOptionIndex = index
                                isAnswered = true
                                if (index == currentQuestion.correctAnswerIndex) score++
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = BorderStroke(1.5.dp, contentColor)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ('A' + index).toString(),
                                        fontWeight = FontWeight.Black,
                                        color = contentColor,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = option,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = contentColor,
                                modifier = Modifier.weight(1f)
                            )
                            if (isAnswered) {
                                if (index == currentQuestion.correctAnswerIndex) {
                                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                } else if (isSelected) {
                                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = isAnswered) {
                Column {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFF57C00)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (isCorrect) getT("correct") else getT("incorrect"),
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentQuestion.explanation,
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                selectedOptionIndex = null
                                isAnswered = false
                            } else {
                                viewModel.completeQuiz(score)
                                onBack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = if (currentQuestionIndex < questions.size - 1) getT("next_question") else getT("finish_quiz"),
                            color = safetyYellow,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
