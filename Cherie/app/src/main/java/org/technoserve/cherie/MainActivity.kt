package org.technoserve.cherie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import org.technoserve.cherie.ui.theme.CherieTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.*
import org.technoserve.cherie.ui.navigation.NavigationItem
import org.technoserve.cherie.ui.screens.HomeScreen
import org.technoserve.cherie.ui.screens.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        setContent {
            CherieTheme {
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




