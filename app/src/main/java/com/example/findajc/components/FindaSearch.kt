package com.example.findajc.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindaSearch(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var counties by remember { mutableStateOf(listOf<Map<String, String>>()) }
    var filteredCounties by remember { mutableStateOf(listOf<Map<String, String>>()) }
    var showDropdown by remember { mutableStateOf(false) }
    val databaseReference = FirebaseDatabase.getInstance().getReference("county")


    LaunchedEffect(Unit) {
        fetchCounties(databaseReference) { loadedCounties ->
            counties = loadedCounties
            filteredCounties = loadedCounties
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                showDropdown = query.isNotEmpty()
                filteredCounties =
                    counties.filter { it["name"]?.contains(query, ignoreCase = true) == true }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            placeholder = { Text("Search County...") },
            shape = CircleShape,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        if (showDropdown) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = 200.dp) // Set a max height for the results
                    .padding(vertical = 8.dp)
                    .background(Color(0xFFEEEEEE))
                    .clip(RoundedCornerShape(8.dp))


            ) {
                LazyColumn {
                    items(filteredCounties.size) { index ->
                        val county =
                            filteredCounties[index] // Get the county from the filtered list
                        DropdownMenuItem(
                            onClick = {
                                showDropdown = false
                                searchQuery = county["name"].orEmpty()
                                handleCountySelected(
                                    navController,
                                    countyId = county["id"].orEmpty()
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "County Icon"
                                )
                            },
                            text = {
                                Text(
                                    text = county["name"].orEmpty(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = MenuItemColors(
                                textColor = Color.Black,
                                leadingIconColor = Color.Black,
                                trailingIconColor = Color.Black,
                                disabledTextColor = Color.Gray,
                                disabledLeadingIconColor = Color.Gray,
                                disabledTrailingIconColor = Color.Gray
                            ),
                            enabled = true,
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        )
                    }
                }
            }
        }

    }
}


private fun handleCountySelected(navController: NavController, countyId: String) {
    // Logic to handle county selection.
    navController.navigate("categoryPage/$countyId")
    Log.d("FindaSearch", "County selected: $countyId")
}

private fun fetchCounties(
    databaseReference: DatabaseReference,
    onCountiesLoaded: (List<Map<String, String>>) -> Unit
) {
    databaseReference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val loadedCounties = snapshot.children.mapNotNull { dataSnapshot ->
                val key = dataSnapshot.key ?: return@mapNotNull null
                val name = dataSnapshot.child("name").getValue(String::class.java)
                    ?: return@mapNotNull null
                mapOf("id" to key, "name" to name)
            }
            onCountiesLoaded(loadedCounties)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FindaSearch", "DatabaseError: ${error.message}")
        }
    })
}

