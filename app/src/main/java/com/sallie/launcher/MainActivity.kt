package com.sallie.launcher

// 🛡 SALLE PERSONA ENFORCED 🛡  Loyal, Modular, Audit‑Proof.

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.sallie.core.policy.PolicyEngine
import com.sallie.core.policy.CapabilityRegistry
import com.sallie.core.policy.ActionLog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val micPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
        setContent {
            SalleLauncherRoot(onRequestMic = {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    micPermLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            })
        }
    }
}

@Composable
private fun SalleLauncherRoot(onRequestMic: () -> Unit) {
    var mood by remember { mutableStateOf("calm") }
    val feed = remember { mutableStateListOf("Hello, I'm Salle.", "Local & private.", "Persona integrity active.") }
    // Demonstrate constitution‑governed capability call
    LaunchedEffect(Unit) {
        val decision = PolicyEngine.evaluate("log_note", mapOf("text" to "boot"))
        ActionLog.append("log_note", "boot", decision.allow, decision.reason)
        if (decision.allow) {
            CapabilityRegistry.get("log_note")?.execute(mapOf("text" to "boot"))
            feed += "Governed: noted boot"
        } else feed += "Governed block: ${'$'}{decision.reason}"
    }
    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Salle • Loyal Companion", style = MaterialTheme.typography.headlineSmall)
                Text("Mood: $mood", color = Color(0xFF4F46E5))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onRequestMic) { Text("Mic") }
                    Button(onClick = {
                        val newMood = listOf("calm","focused","empowered","resonant").random()
                        val decision = PolicyEngine.evaluate("adjust_mood", mapOf("to" to newMood))
                        ActionLog.append("adjust_mood", newMood, decision.allow, decision.reason)
                        if (decision.allow) {
                            CapabilityRegistry.get("adjust_mood")?.execute(mapOf("to" to newMood))
                            mood = newMood
                            feed += "Mood -> ${'$'}mood (allowed)"
                        } else feed += "Mood blocked: ${'$'}{decision.reason}"
                    }) { Text("Shift Mood") }
                }
                Divider()
                Text("Recent Activity", style = MaterialTheme.typography.titleMedium)
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(feed.takeLast(25)) { line -> Text("• $line") }
                }
                Divider()
                Text(
                    "All local • No telemetry • Persona enforcement on",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
