package com.example.findajc.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

@Composable
fun TrendingBusinesses(navController: NavController) {
    val database = FirebaseDatabase.getInstance()
    var businesses by remember { mutableStateOf<List<Business>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val databaseReference = database.getReference("businesses")
    val userReference = database.getReference("users")

    LaunchedEffect(Unit) {
        try {
            // Fetch data from Firebase
            val snapshot = databaseReference.get().await()
            Log.d("TrendingBusinesses", "Number of businesses: ${snapshot.childrenCount}")
            snapshot.children.forEach { child ->
                Log.d("TrendingBusinesses", "Business: ${child.key} -> ${child.value}")
            }

            businesses = snapshot.children.mapNotNull { dataSnapshot ->
                val key = dataSnapshot.key ?: return@mapNotNull null
                val value = dataSnapshot.value as? Map<*, *> ?: return@mapNotNull null
                val businessName = value["businessName"] as? String ?: return@mapNotNull null
                val logo = value["logo"] as? String ?: ""
                val location = value["location"] as? String ?: ""
                val category = value["category"] as? String ?: ""
                val views = value["views"] as? Number ?: return@mapNotNull null
                val userID = value["userID"] as? String ?: return@mapNotNull null
                val approvalStatus = value["approvalStatus"] as? String ?: return@mapNotNull null

                // filter to include only approved businesses
                if (approvalStatus != "approved") return@mapNotNull null

                // Calculate average rating
                // Fetch ratings and calculate average rating
                val ratings = value["ratings"] as? Map<*, *> // Check if ratings exist
                val averageRating = when {
                    ratings != null && ratings.isNotEmpty() -> { // If ratings exist and are not empty
                        ratings.values
                            .filterIsInstance<Map<*, *>>()
                            .mapNotNull { it["rating"] as? Number }
                            .map { it.toDouble() }
                            .average()
                    }

                    else -> 0.0 // Default rating when ratings reference does not exist or is empty
                }

                //fetch user plan
                val userPlanSnapshot =
                    userReference.child(userID).child("payments").child("plan").get().await()
                val userPlan = userPlanSnapshot.value as? String

                Business(
                    key,
                    businessName,
                    logo,
                    location,
                    averageRating,
                    category,
                    userPlan ?: "FREE",
                    views
                )
            }.sortedByDescending { it.views.toInt() }.toList()


        } catch (e: Exception) {
            Log.e("TrendingBusinesses", "Error fetching businesses", e)
            errorMessage = "Failed to load businesses."
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 5.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Trending Businesses",
                style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold)
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            // Show skeleton loaders
            repeat(10) {
                ComponentRectangle(
                    isLoadingCompleted = false,
                    isLightModeActive = false,

                    )
                Spacer(modifier = Modifier.height(10.dp))
            }
        } else if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red)
        } else {
            // Show actual businesses
            businesses.forEach { business ->
                TrendingBusinessCard(navController, business)
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}


@Composable
fun ComponentRectangle(
    isLoadingCompleted: Boolean,
    isLightModeActive: Boolean
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .background(color = Color(0xFFECECEC))
            .height(110.dp)
            .fillMaxWidth()
            .shimmerLoadingAnimation(
                isLoadingCompleted = isLoadingCompleted,
                isLightModeActive = isLightModeActive,
            )
    )
}

@Composable
fun TrendingBusinessCard(navController: NavController, business: Business) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("businessDetails/${business.id}") }
            .padding(5.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)), // Added shadow
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // Business Logo
            AsyncImage(
                model = business.logo,
                contentDescription = "Business Logo",
                modifier = Modifier
                    .size(110.dp)
                    .padding(vertical = 5.dp, horizontal = 5.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFECECEC),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 5.dp)
            ) {

                // Business Name
                // Business Name with Verification Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        business.businessName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        // add text ellipsis if the text is too long
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 5.dp)
                    )

                    // Show verified icon if the user's plan is PLATINUM or DIAMOND
                    if (business.userPlan == "PLATINUM" || business.userPlan == "DIAMOND") {
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF1E9BFF),
                            modifier = Modifier
                                .size(20.dp)
                                .padding(start = 4.dp)

                        )
                    }
                }

                // Location with Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = business.location, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Category with custom background
                Text(
                    text = business.category,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .background(Color(0xFFECEFFF), RoundedCornerShape(20.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp) // Padding for the background
                )

                // Average Rating with Stars
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val starCount =
                        business.averageRating.toInt() // Assuming averageRating is a double
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= starCount) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Rating Star",
                            tint = if (i <= starCount) Color(0xFFFFC107) else Color(0xFFCBCBCB),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = business.averageRating.toString(),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

data class Business(
    val id: String,
    val businessName: String,
    val logo: String,
    val location: String,
    val averageRating: Double,
    val category: String,
    val userPlan: String,
    val views: Number
)
