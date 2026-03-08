package com.example.intentsday4

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.intentsday4.ui.theme.IntentsDay4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IntentsDay4Theme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    QuickNoteScreen(this)
                }
            }
        }
    }
}


@Composable
fun QuickNoteScreen(context: Context) {
    var note by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

// Register the image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Enter your note") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            shareText(context, note)
        }) {
            Text("Share Note")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            imagePickerLauncher.launch("image/*")
        }) {
            Text("Select Image from Gallery")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                selectedImageUri?.let { shareImage(context,it) }
            },
            enabled = selectedImageUri != null
        ) {
            Text("Share Selected Image")
        }

        Button(onClick = {
            scheduleReminder(context)
        }) {
            Text("Remind Me in 1 Minute")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val serviceIntent = Intent(context, MyForegroundService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }) {
            Text("Start Upload Service")
        }
    }
}

private fun shareImage(context: Context ,imageUri: Uri) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

//    val context = LocalContext.current
    context.startActivity(Intent.createChooser(shareIntent, "Share image via"))
}

fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share note via"))
}




