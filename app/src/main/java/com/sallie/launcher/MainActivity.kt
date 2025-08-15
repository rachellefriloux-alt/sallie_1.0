package com.sallie.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import androidx.core.content.ContextCompat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // log is internal; UI will show via button actions
        }
        setContent { RootSallieApp(onRequestMic = {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermission.launch(Manifest.permission.RECORD_AUDIO)
            }
        }) }

    // Schedule periodic export every 6 hours
    val work = PeriodicWorkRequestBuilder<ConversationExportWorker>(6, TimeUnit.HOURS).build()
    WorkManager.getInstance(this).enqueue(work)
    }
}

@Composable
@Composable
fun RootSallieApp(onRequestMic: () -> Unit, vm: SallieViewModel = viewModel()) {
    val theme by vm.theme.collectAsState()
    val scheme = ThemeColorsMapper.schemeFor(theme)
    MaterialTheme(colorScheme = scheme) {
        SallieHome(onRequestMic = onRequestMic, vm = vm)
    }
}

fun SallieHome(onRequestMic: () -> Unit, vm: SallieViewModel = viewModel()) {
    val mood by vm.mood.collectAsState()
    val fatigue by vm.fatigue.collectAsState()
    val tasks by vm.tasks.collectAsState()
    val situation by vm.situation.collectAsState()
    val dignityEvents by vm.dignityEvents.collectAsState()
    val log by vm.log.collectAsState()
    val theme by vm.theme.collectAsState()
    val listening by vm.listening.collectAsState()
    val voice by vm.voice.collectAsState()
    val metrics by vm.featureMetrics.collectAsState()
    val transcript = if (listening) vm.run { system.asrManager.getTranscript() } else ""
    val rms = if (listening) vm.run { system.asrManager.rmsLevel() } else 0f
    val finals = if (listening) vm.run { system.asrManager.allFinal().size } else 0
    val rmsSeries = if (listening) vm.run { system.asrManager.rmsSeries() } else emptyList()
    val conversation by vm.conversation.collectAsState()
    val asrError by vm.asrError.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Sallie Dashboard - Theme: $theme", style = MaterialTheme.typography.headlineSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Mood: $mood")
                Text("Fatigue: $fatigue")
                Button(onClick = { vm.heartbeat() }) { Text("Pulse") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.updateMood("focused") }) { Text("Focused") }
                Button(onClick = { vm.updateMood("calm") }) { Text("Calm") }
                Button(onClick = { vm.updateMood("energetic") }) { Text("Energetic") }
                Button(onClick = { vm.setFatigue((0..10).random()) }) { Text("Rand Fatigue") }
                Button(onClick = onRequestMic) { Text("Voice Perm") }
            }
            Divider()
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.seedTasks() }) { Text("Plan Tasks") }
                Button(onClick = { vm.analyzeSituation("Urgent conflict deadline") }) { Text("Analyze Situation") }
                Button(onClick = { vm.enforceDignity("redact-sensitive") }) { Text("Enforce Dignity") }
                Button(onClick = { vm.refreshMetrics() }) { Text("Metrics") }
                Button(onClick = {
                    val csv = vm.exportConversationCsv()
                    vm.appendLogExternal("Exported ${csv.lineSequence().count()} lines")
                }) { Text("Export Conv") }
                Button(onClick = {
                    val csv = vm.getConversationExport()
                    val exportFile = File(cacheDir, "conversation_export.csv")
                    exportFile.writeText(csv)
                    val uri = FileProvider.getUriForFile(this@MainActivity, "${packageName}.fileprovider", exportFile)
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share Conversation"))
                }) { Text("Share Conv") }
                Button(onClick = {
                    val json = vm.exportConversationJson(limit = 20)
                    vm.appendLogExternal("JSON last20 size=${json.length}")
                }) { Text("JSON 20") }
                Button(onClick = {
                    val jsonUser = vm.exportConversationJson(speaker = "user", limit = 10)
                    vm.appendLogExternal("JSON user10 size=${jsonUser.length}")
                }) { Text("JSON User") }
            }
            Text("Situation: $situation")
            Text("Dignity Events: $dignityEvents")
            if (tasks.isNotEmpty()) {
                Text("Selected Tasks:")
                tasks.forEach { t -> Text("• ${t.title} (${t.estimatedMinutes}m)") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.simulateDeviceAction(blocked = true) }) { Text("Blocked Call") }
                Button(onClick = { vm.simulateDeviceAction(blocked = false) }) { Text("Allowed Call") }
            }
            Divider()
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { vm.toggleListening() }) { Text(if (listening) "Stop ASR" else "Start ASR") }
                Button(onClick = { vm.captureTranscript() }) { Text("Grab Transcript") }
                Text(if (listening) "Listening..." else "Idle")
            }
            if (asrError != null) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ASR Error: $asrError", color = Color.Red)
                    Button(onClick = { vm.clearAsrError() }) { Text("Dismiss") }
                }
            }
            if (transcript.isNotBlank()) Text("Transcript: $transcript (rms=${"%.1f".format(rms)} final=$finals)")
            if (rmsSeries.isNotEmpty()) {
                Waveform(rmsSeries)
            }
            if (conversation.isNotEmpty()) {
                Text("Conversation (${conversation.size})")
                LazyColumn(modifier = Modifier.height(180.dp)) {
                    items(conversation) { entry ->
                        val speakerColor = if (entry.speaker == "user") Color(0xFF2196F3) else Color(0xFF9C27B0)
                        Text(
                            text = "${entry.speaker}: ${entry.text}",
                            color = speakerColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.speak("Hello, I'm Sallie") }) { Text("Speak Hello") }
                Button(onClick = { vm.switchVoice("warm") }) { Text("Voice: Warm") }
                Button(onClick = { vm.switchVoice("crisp") }) { Text("Voice: Crisp") }
                Text("Voice=$voice")
            }
            if (metrics.isNotEmpty()) {
                Text("Metrics: ${metrics.entries.joinToString { it.key+":"+it.value }}")
            }
            Divider()
            Text("Recent Log:")
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(log) { entry -> Text(entry) }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSallieHome() { RootSallieApp(onRequestMic = {}) }

@Composable
private fun Waveform(levels: List<Float>, modifier: Modifier = Modifier.fillMaxWidth().height(40.dp)) {
    val bars = levels.takeLast(100)
    val max = (bars.maxOrNull() ?: 1f).coerceAtLeast(1f)
    Canvas(modifier = modifier) {
        val barWidth = size.width / bars.size.coerceAtLeast(1)
        bars.forEachIndexed { idx, v ->
            val norm = (v / max).coerceIn(0f, 1f)
            val barHeight = size.height * norm
            drawRect(
                color = Color(0xFF4CAF50),
                topLeft = androidx.compose.ui.geometry.Offset(x = idx * barWidth, y = size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(width = barWidth * 0.7f, height = barHeight)
            )
        }
    }
}
