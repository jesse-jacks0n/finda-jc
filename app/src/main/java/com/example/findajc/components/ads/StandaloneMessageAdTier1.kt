package com.example.findajc.components.ads

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandaloneMessageAd() {

    val database = Firebase.database
    val messagesReference = database.getReference("messages/tier1")

    var messages by remember { mutableStateOf<List<Pair<String, Message>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var currentIndex by remember { mutableIntStateOf(0) } // Index for carousel

    // Fetch messages from Firebase
    LaunchedEffect(Unit) {
        fetchMessages(messagesReference) { fetchedMessages ->
            messages = fetchedMessages
            loading = false
        }
    }

    // Carousel effect to change message every 5 seconds
    LaunchedEffect(currentIndex) {
        if (loading || messages.isEmpty()) return@LaunchedEffect
        delay(5000)
        currentIndex = (currentIndex + 1) % messages.size // Cycle through messages
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (messages.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No messages available at the moment.")
        }
        return
    }


    Text(text = "Powered by Finda", style = MaterialTheme.typography.bodySmall)
    Spacer(modifier = Modifier.height(12.dp))
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(messages) { (_, message) ->
            MessageCard(message)
        }
    }
}

@Composable
fun MessageCard(message: Message) {
    Card(
        modifier = Modifier
            .width(350.dp)
            .padding(5.dp),
        shape = RoundedCornerShape(1.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            if (message.imageUrl.isNotEmpty()) {

                AsyncImage(
                    model = message.imageUrl,
                    contentDescription = message.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = message.title, style = MaterialTheme.typography.headlineSmall)
                if (message.senderName.isNotEmpty()) {
                    Text(text = message.senderName, style = MaterialTheme.typography.bodySmall)
                }
                Text(text = message.message, style = MaterialTheme.typography.bodyMedium)
                if (message.link.isNotEmpty()) {
                    Text(text = message.link, color = MaterialTheme.colorScheme.primary)
                }
            }

        }
    }
}

private suspend fun fetchMessages(
    ref: DatabaseReference,
    onResult: (List<Pair<String, Message>>) -> Unit
) {
    try {
        val snapshot = ref.get().await()
        val messageData = snapshot.value as? Map<*, *>
        val messagesArray = messageData?.mapNotNull { entry ->
            val key = entry.key as? String ?: return@mapNotNull null
            val value = entry.value as? Map<*, *> ?: return@mapNotNull null
            val message = Message(
                title = value["title"] as? String ?: "",
                senderName = value["senderName"] as? String ?: "",
                message = value["message"] as? String ?: "",
                imageUrl = value["imageUrl"] as? String ?: "",
                link = value["link"] as? String ?: ""
            )
            key to message
        } ?: emptyList()

        onResult(messagesArray)
    } catch (e: Exception) {
        Log.e(
            "com.example.findajc.components.ads.StandaloneMessageAd",
            "Error fetching messages: ",
            e
        )
        onResult(emptyList()) // Return an empty list on error
    }
}

data class Message(
    val title: String,
    val senderName: String,
    val message: String,
    val imageUrl: String,
    val link: String
)
