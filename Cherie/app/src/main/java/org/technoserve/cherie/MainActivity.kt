package org.technoserve.cherie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.technoserve.cherie.ui.theme.CherieTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.technoserve.cherie.ui.navigation.NavigationItem
import org.technoserve.cherie.ui.screens.HomeScreen
import org.technoserve.cherie.ui.screens.ProfileScreen

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        setContent {
            CherieTheme {
                // A surface container using the 'background' color from the theme
                val multiplePermissionsState = rememberMultiplePermissionsState(
                    listOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                    )
                )
                Surface(color = MaterialTheme.colors.background) {
                    AppRoot()
//                    Sample(
//                        multiplePermissionsState,
//                        navigateToSettingsScreen = {
//                            startActivity(
//                                Intent(
//                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                                    Uri.fromParts("package", packageName, null)
//                                )
//                            )
//                        }
//                    )
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




@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Sample(
    multiplePermissionsState: MultiplePermissionsState,
    navigateToSettingsScreen: () -> Unit
) {
    // Track if the user doesn't want to see the rationale any more.
    var doNotShowRationale = rememberSaveable { mutableStateOf(false) }

    when {
        // If all permissions are granted, then show screen with the feature enabled
        multiplePermissionsState.allPermissionsGranted -> {
            Text("Camera and Read storage permissions Granted! Thank you!")
        }
        // If the user denied any permission but a rationale should be shown, or the user sees
        // the permissions for the first time, explain why the feature is needed by the app and
        // allow the user decide if they don't want to see the rationale any more.
        multiplePermissionsState.shouldShowRationale ||
                !multiplePermissionsState.permissionRequested ->
        {
            if (doNotShowRationale.value) {
                Text("Feature not available")
            } else {
                Column {
                    val revokedPermissionsText = getPermissionsText(
                        multiplePermissionsState.revokedPermissions
                    )
                    Text(
                        "$revokedPermissionsText important. " +
                                "Please grant all of them for the app to function properly."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(
                            onClick = {
                                multiplePermissionsState.launchMultiplePermissionRequest()
                            }
                        ) {
                            Text("Request permissions")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { doNotShowRationale.value = true }) {
                            Text("Don't show rationale again")
                        }
                    }
                }
            }
        }
        // If the criteria above hasn't been met, the user denied some permission. Let's present
        // the user with a FAQ in case they want to know more and send them to the Settings screen
        // to enable them the future there if they want to.
        else -> {
            Column {
                val revokedPermissionsText = getPermissionsText(
                    multiplePermissionsState.revokedPermissions
                )
                Text(
                    "$revokedPermissionsText denied. See this FAQ with " +
                            "information about why we need this permission. Please, grant us " +
                            "access on the Settings screen."
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = navigateToSettingsScreen) {
                    Text("Open Settings")
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun getPermissionsText(permissions: List<PermissionState>): String {
    val revokedPermissionsSize = permissions.size
    if (revokedPermissionsSize == 0) return ""

    val textToShow = StringBuilder().apply {
        append("The ")
    }

    for (i in permissions.indices) {
        textToShow.append(permissions[i].permission)
        when {
            revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                textToShow.append(", and ")
            }
            i == revokedPermissionsSize - 1 -> {
                textToShow.append(" ")
            }
            else -> {
                textToShow.append(", ")
            }
        }
    }
    textToShow.append(if (revokedPermissionsSize == 1) "permission is" else "permissions are")
    return textToShow.toString()
}

