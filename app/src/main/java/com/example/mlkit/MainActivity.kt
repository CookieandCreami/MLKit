package com.example.mlkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mlkit.ui.theme.MLKitTheme
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MLKitTheme {
                Greeting()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Greeting() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {

        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("번역할 내용을 입력하세요") }
        )

        var isDownloading by remember { mutableStateOf(false) }

        val koEnTranslator = remember {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.KOREAN)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
            Translation.getClient(options)
        }

        downloadModel(koEnTranslator, onSuccess = {
            isDownloading = true
        })

        var outPutText by remember { mutableStateOf("") }

        Button(onClick = {
            koEnTranslator.translate(text)
                .addOnSuccessListener { translatedText ->
                    outPutText = translatedText
                }
                .addOnFailureListener { exception ->
                    // Error.
                    // ...
                }
        }, enabled = isDownloading) {
            Text(text = "번역")
        }
        Text(text = outPutText)
    }
}

@Composable
fun downloadModel(koEnTranslator: Translator,
                  onSuccess: () -> Unit,) {
    LaunchedEffect(key1 = koEnTranslator) {
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        koEnTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                onSuccess()
            }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MLKitTheme {
        Greeting()
    }
}