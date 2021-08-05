package org.technoserve.cherie.ui.screens

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import org.technoserve.cherie.R

@Composable
fun ProfileScreen() {

    val context = LocalContext.current as Activity

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                Log.d("USER", user.uid)
            }
            // ...
        } else {
            if(response == null){

            }
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    // See: https://developer.android.com/training/basics/intents/result
    val signInLauncher = rememberLauncherForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        onSignInResult(res)
    }
//
//    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
    )

// Create and launch sign-in intent
    LaunchedEffect(providers) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.cherry)
                .setTheme(R.style.Theme_Cherie)
                .build()
            signInLauncher.launch(signInIntent)
        } else {
            Log.d("USER", user.uid)
            val fileName = "dummy.jpg"
            val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
            val imageUri = Uri.parse("android.resource://org.technoserve.cherie/drawable/cherry")
            storageReference.putFile(imageUri).addOnSuccessListener {
                Log.d("UPLOAD", "Uploaded successfully" + it.uploadSessionUri.toString())
            }.addOnFailureListener {
                Log.d("UPLOAD", "Upload Failed")
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
                .background(MaterialTheme.colors.background)
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = "Profile",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 25.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    AuthUI.getInstance()
                        .signOut(context)
                        .addOnCompleteListener {
                            Log.d("LOGOUT RESULT: ", it.result.toString())
                        }
                },
                modifier = Modifier.requiredWidth(160.dp),
                shape = RoundedCornerShape(0),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 4.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Text(
                    text = "Logout",
                    modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}