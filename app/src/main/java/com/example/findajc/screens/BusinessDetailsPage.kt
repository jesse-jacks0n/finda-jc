package com.example.findajc.screens

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await

data class BusinessDetails(
    val details: Map<String, Any?>,
    val services: List<String> = emptyList(),
    val timestamp: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDetailsPage(businessId: String, navController: NavController, context: Context) {

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("BusinessDetailsCache", Context.MODE_PRIVATE)

    var businessDetails by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var serviceNames by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Database reference to the business details in Firebase
    val databaseReference =
        FirebaseDatabase.getInstance().getReference("businesses/$businessId")

    LaunchedEffect(businessId) {
        try {
            // Load business details
            val snapshot = databaseReference.get().await()
            Log.d("BusinessDetailsPage", "Snapshot Data: $snapshot")

            // Check if the snapshot exists and has valid data
            if (snapshot.exists()) {
                businessDetails = (snapshot.value as? Map<*, *>)
                    ?.mapKeys { (key, _) -> key.toString() }
                        as? Map<String, Any>
                Log.d("BusinessDetailsPage", "Business Details: $businessDetails")

                // Fetch and collect service names
                val servicesIds =
                    (businessDetails?.get("products") as? List<*>)?.mapNotNull { it as? String }
                        ?: emptyList()
                Log.d("BusinessDetailsPage", "Services IDs: $servicesIds")

                // Fetch service names and cache
                // Collect service names concurrently
                serviceNames = servicesIds.map { serviceId ->
                    async {
                        Log.d("BusinessDetailsPage", "Fetching service name for ID: $serviceId")
                        val reference =
                            FirebaseDatabase.getInstance()
                                .getReference("productsServices/$serviceId")
                        val serviceName = reference.get().await().child("name").value as? String
                            ?: "Unknown Service"
                        Log.d("BusinessDetailsPage", "Fetched service name: $serviceName")
                        serviceName
                    }
                }.awaitAll()


                Log.d("BusinessDetailsPage", "Fetched Services Names: $serviceNames")

                // Cache the fetched data
                val businessDetailsToCache = BusinessDetails(
                    businessDetails ?: emptyMap(),
                    serviceNames,
                    System.currentTimeMillis()
                )
                sharedPreferences.edit()
                    .putString(businessId, Gson().toJson(businessDetailsToCache)).apply()
            } else {
                Log.w("BusinessDetailsPage", "Business not found for ID: $businessId")
            }
        } catch (e: Exception) {
            Log.e("BusinessDetailsPage", "Error fetching business details", e)
        } finally {
            isLoading = false // Set loading to false after the fetch attempt
        }
    }

    // Scaffold and UI rendering
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Business Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                businessDetails?.let { details ->
                    BusinessDetailsCard(details = details)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Gallery
                    GalleryComposable(details = details)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Services and products Card
                    ServicesAndProductsCard(servicesNames = serviceNames)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Working Schedules Card
                    WorkingSchedulesCard(details = details)
                }
            }
        }
    }
}

@Composable
fun BusinessDetailsCard(details: Map<String, Any?>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEEEEEE),
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.padding(5.dp)) {
                AsyncImage(
                    model = details["logo"] as? String ?: "",
                    contentDescription = "Business Logo",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = details["businessName"] as? String ?: "No Name",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "Operates in ", fontSize = 24.sp)
                Text(text = formatLocation(details), fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = details["mobileNumber"] as? String ?: "N/A",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = details["description"] as? String ?: "No Description",
                textAlign = TextAlign.Justify,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun GalleryComposable(details: Map<String, Any?>) {
    val photos = (details["photos"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
    Log.d("GalleryComposable", "Photos: $photos")
    var showGallery by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)

    ) {
        // Button to toggle gallery visibility
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(onClick = { showGallery = !showGallery }) {
                Text(text = if (showGallery) "Hide Gallery" else "Show Gallery")
            }
        }

        // Show the gallery only if showGallery is true
        if (showGallery && photos.isNotEmpty()) {
            val columns = 3 // Define your desired number of columns
            val rows = (photos.size + columns - 1) / columns // Calculate the number of rows
            Column {
                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (column in 0 until columns) {
                            val index = row * columns + column
                            if (index < photos.size) {
                                val photoUrl = photos[index]
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Gallery Image",
                                    modifier = Modifier
                                        .weight(1f) // Equal weight for each image column
                                        .aspectRatio(1f)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(15.dp)),

                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Optionally you can fill empty spaces with a placeholder or simply skip
                                // Empty space if no image
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else if (showGallery) {
            Text(text = "No images available", fontStyle = FontStyle.Italic)
        }
    }
}

@Composable
fun ServicesAndProductsCard(servicesNames: List<String>?) {
    // Set isLoading to false immediately since we're using passed servicesNames
    val isEmpty: Boolean = servicesNames.isNullOrEmpty()

    // Card to display services and products
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEEEEEE),
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Services and Products",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (isEmpty) {
                Text(
                    text = "No Services or Products",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            } else {
                servicesNames?.forEach { serviceName ->
                    Text(
                        text = "• $serviceName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
fun WorkingSchedulesCard(details: Map<String, Any?>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEEEEEE),
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Working Schedules",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "• Weekdays: ${details["weekdays"] as? String ?: "N/A"}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "• Saturdays: ${details["saturday"] as? String ?: "N/A"}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "• Sundays: ${details["sunday"] as? String ?: "N/A"}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}


private fun formatLocation(details: Map<String, Any?>): String {
    val county = details["county"] as? String ?: "N/A"
    val location = details["location"] as? String ?: "N/A"
    return "$county, $location"
}
