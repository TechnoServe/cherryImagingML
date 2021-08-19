package org.technoserve.cherie.ui.screens

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.technoserve.cherie.Preferences
import org.technoserve.cherie.R
import org.technoserve.cherie.database.Prediction
import org.technoserve.cherie.database.PredictionViewModel
import org.technoserve.cherie.database.PredictionViewModelFactory
import org.technoserve.cherie.ui.components.ButtonPrimary

@Composable
fun ProfileScreen(
    scaffoldState: ScaffoldState,
    homeScope: CoroutineScope
) {

    val context = LocalContext.current as Activity

    val predictionViewModel: PredictionViewModel = viewModel(
        factory = PredictionViewModelFactory(context.applicationContext as Application)
    )

    val listItems = predictionViewModel.readAllData.observeAsState(listOf()).value

    var user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

    val sharedPrefs by remember { mutableStateOf(Preferences(context)) }

    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build(),
    )

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val newUser = FirebaseAuth.getInstance().currentUser
            if (newUser != null) {
                user = newUser
                homeScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("Login Successful")
                }
            }
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            homeScope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Login Failed")
            }
        }
    }

    // See: https://developer.android.com/training/basics/intents/result
    val signInLauncher =
        rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
            onSignInResult(res)
        }

    fun startAuthFlow() {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.cherie)
            .setTheme(R.style.LoginTheme)
            .build()
        signInLauncher.launch(signInIntent)
    }

    fun logout() {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                val message = if (it.isSuccessful) "Logout Successful" else "Logout Failed"
                user = null
                homeScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
            }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        color = Color.White,
                        fontSize = 18.sp,
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.Black,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp)
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            if (user == null) {
                ButtonPrimary(onClick = { startAuthFlow() }, label = "Log in")
                Spacer(modifier = Modifier.height(64.dp))
            }

            user?.let { UserInfo(it) }

            Stats(listItems, sharedPrefs)

            if (user != null) {
                Spacer(modifier = Modifier.weight(1f))
                ButtonPrimary(onClick = { logout() }, label = "Log out")
                Spacer(modifier = Modifier.height(64.dp))
            }

        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    val scaffoldState = rememberScaffoldState()
    ProfileScreen(homeScope = GlobalScope, scaffoldState = scaffoldState)
}

@Composable
fun UserInfo(user: FirebaseUser) {
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        if(user.email != null){
            Text("Email: " + user.email)
            Text("Name: " + user.displayName)
        }
        if(user.phoneNumber != null){
            Text("Phone: " + user.phoneNumber)
        }
        Text(
            text = "Region: " + stringResource(id = R.string.app_region),
            fontSize = 12.sp
        )
        Text(
            text = "Last Sign In: " + DateTime(user.metadata?.lastSignInTimestamp).toString(fmt),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun Stats(listItems: List<Prediction>, sharedPrefs: Preferences) {
    var averageRipe = 0f
    var averageUnderripe = 0f
    var averageOverripe = 0f
    if(listItems.isNotEmpty()){
        for(item in listItems){
            averageRipe += item.ripe
            averageUnderripe += item.underripe
            averageOverripe += item.overripe
        }
        averageRipe /= listItems.size
        averageUnderripe /= listItems.size
        averageOverripe /= listItems.size
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Average Ripeness Stats", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier=Modifier.weight(1f)){
                Stat(title = "Ripe", value = "${String.format("%.0f", averageRipe)}%", color= Color.Red)
            }
            StatDivider()
            Box(modifier=Modifier.weight(1f)){
                Stat(title = "Underripe", value = "${String.format("%.0f", averageUnderripe)}%")
            }
            StatDivider()
            Box(modifier=Modifier.weight(1f)){
                Stat(title = "Overripe", value = "${String.format("%.0f", averageOverripe)}%")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Usage Stats", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier=Modifier.weight(1f)){
                Stat(title = "Generated\nPrediction${if(sharedPrefs.generatedPredictions == 1) "" else "s"}", value = sharedPrefs.generatedPredictions.toString())
            }
            StatDivider()
            Box(modifier=Modifier.weight(1f)){
                Stat(title = "Saved\nPrediction${if(listItems.size == 1) "" else "s"}", value = listItems.size.toString())
            }
            StatDivider()
            Box(modifier=Modifier.weight(1f)){
                Stat(title = "Upload${if(sharedPrefs.uploadedPredictions == 1) "" else "s"}\n", value = sharedPrefs.uploadedPredictions.toString())
            }
        }
    }
}

@Composable
fun Stat(title: String, value: String, color: Color = MaterialTheme.colors.onSurface) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = value, fontSize = 36.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun StatDivider() {
    Spacer(modifier = Modifier.width(8.dp))
    Divider(
        color = Color.LightGray,
        modifier = Modifier
            .width(1.dp)
            .height(64.dp)
            .padding(vertical = 4.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
}