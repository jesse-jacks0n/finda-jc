package com.example.findajc.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

data class Category(val name: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesDetailsPage(countyId: String, navController: NavController, context: Context) {
    var categories by remember { mutableStateOf(listOf<Category>()) }
    val databaseReference = FirebaseDatabase.getInstance().getReference("category")

    LaunchedEffect(countyId) {
        fetchCategories(databaseReference) { fetchedCategories ->
            categories = fetchedCategories
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Categories") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            categories.forEach { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEEEEEE),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    )


                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = category.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp)) // Space between title and description
                        Text(
                            text = category.description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

private fun fetchCategories(
    databaseReference: DatabaseReference,
    onCategoriesLoaded: (List<Category>) -> Unit
) {
    databaseReference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val loadedCategories = snapshot.children.mapNotNull { dataSnapshot ->
                val name = dataSnapshot.child("name").getValue(String::class.java)
                val description = dataSnapshot.child("description").getValue(String::class.java)
                if (name != null && description != null) {
                    Category(name, description)
                } else {
                    null
                }
            }
            onCategoriesLoaded(loadedCategories)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("CategoriesDetailsPage", "DatabaseError: ${error.message}")
        }
    })
}
