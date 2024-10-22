package com.example.findajc.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findajc.R


@Composable
fun MyScreenContent() {
    GradientBackground {
        // Your content goes here
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Hello, World!", color = Color.White, fontSize = 24.sp)
            // You can add more UI elements here
        }
    }
}

@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image from res/drawable
        val backgroundImage: Painter =
            painterResource(id = R.drawable.bg) // Change to your image file name

        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Blurred Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 5.dp)
                .background(Color.White.copy(alpha = 0.9f)) // Change the color and alpha for your desired effect
        )

        content()
    }
}

// Preview
@Preview
@Composable
fun MyScreenPreview() {
    MyScreenContent()
}
