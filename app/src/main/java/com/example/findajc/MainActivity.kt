package com.example.findajc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.findajc.pages.Dashboard
import com.example.findajc.pages.ExplorePage
import com.example.findajc.pages.JobsPage
import com.example.findajc.pages.JoinPage
import com.example.findajc.screens.BusinessDetailsPage
import com.example.findajc.screens.CategoriesDetailsPage
import com.example.findajc.ui.theme.FindaJcTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this) {}
        setContent {
            FindaJcTheme {
                SetupNavGraph()
            }
        }
    }
}

@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomePage(navController) }


        // Define route for business details
        composable("businessDetails/{businessId}") { backStackEntry ->
            val businessId = backStackEntry.arguments?.getString("businessId") ?: return@composable
            val context = LocalContext.current
            BusinessDetailsPage(businessId, navController, context)
        }
        //category details
        composable("categoryPage/{countyId}") { backStackEntry ->
            val countyId = backStackEntry.arguments?.getString("countyId") ?: return@composable
            val context = LocalContext.current
            CategoriesDetailsPage(countyId, navController, context)
        }
    }
}

@Composable
fun HomePage(navController: NavController) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val items = listOf("Dashboard", "Explore", "Jobs", "Join")
    val icons = listOf(
        painterResource(id = R.drawable.home),
        painterResource(id = R.drawable.search),
        painterResource(id = R.drawable.briefcase),
        painterResource(id = R.drawable.adduser)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(76.dp),
                tonalElevation = 8.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Image(
                                painter = icons[index],
                                contentDescription = item,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(item) },
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedIndex) {
            0 -> Dashboard(navController = navController, Modifier.padding(innerPadding))
            1 -> ExplorePage(Modifier.padding(innerPadding))
            2 -> JobsPage(Modifier.padding(innerPadding))
            3 -> JoinPage(Modifier.padding(innerPadding))
        }
    }
}

