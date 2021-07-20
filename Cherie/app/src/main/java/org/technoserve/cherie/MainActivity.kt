package org.technoserve.cherie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import org.technoserve.cherie.ui.theme.ComposeGenesisTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.*
import androidx.navigation.navigation
import org.technoserve.cherie.ui.navigation.NavigationItem
import org.technoserve.cherie.ui.screens.HomeScreen
import org.technoserve.cherie.ui.screens.InferenceScreen
import org.technoserve.cherie.ui.screens.ProfileScreen
import org.technoserve.cherie.ui.screens.SavedPredictionsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        setContent {
            ComposeGenesisTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Home.route
    ) {
        composable(NavigationItem.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(NavigationItem.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }

}




