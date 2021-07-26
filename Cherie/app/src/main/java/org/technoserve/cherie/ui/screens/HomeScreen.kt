package org.technoserve.cherie.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.technoserve.cherie.ui.navigation.BottomNavigationBar
import org.technoserve.cherie.ui.navigation.NavigationItem
import org.technoserve.cherie.R

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        bottomBar = { BottomNavigationBar(navController) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .wrapContentSize(Alignment.Center)
        ) {
            newUI()
        }
        Navigation(navController)



    }

}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Inference.route) {
        composable(NavigationItem.Inference.route) {
            InferenceScreen()
        }
        composable(NavigationItem.Logs.route) {
            SavedPredictionsScreen()
        }
        composable(NavigationItem.Profile.route) {
            ProfileScreen()
        }
    }
}


@Composable
fun newUI(){
    MaterialTheme {

        val Shapes = Shapes(
            small = RoundedCornerShape(percent = 50),
            medium = RoundedCornerShape(0f),
            large = CutCornerShape(
                topStart = 16.dp,
                topEnd = 0.dp
            )
        )
        Column(
            modifier = Modifier.padding(16.dp)
        )
        {
            Image(
                painter = painterResource(R.drawable.cherry),
                contentDescription = null,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(16.dp))
            MaterialTheme(shapes = Shapes, ){
                allButtons()
            }
           }
    }
}

// In order to use compose properties annotate with @Compose
@Composable
fun allButtons() {
    // Here we are using Row to add
    // two buttons in single Column
    // You can change it as per yur need
    val typography = MaterialTheme.typography
    Column {
        // Create a Main Button or Normal Button
        Image(painter = painterResource(id = R.drawable.cherry),
            contentDescription = null)
        // Create a Text Button
        TextButton(onClick = { /*TODO*/ }, modifier = Modifier.padding(8.dp)) {
            Text(text = "Load Image",
                textAlign = TextAlign.Center,
                style = typography.body2)
        }
    }
    Column {
        Image(painter = painterResource(id = R.drawable.cherry),
            contentDescription = null)
        // Create a Text Button
        TextButton(onClick = { /*TODO*/ }, modifier = Modifier.padding(8.dp)) {
            Text(text = "Take Picture",
                textAlign = TextAlign.Center,
                style = typography.body2)
        }

    }

}

// To see the preview annotate with @Preview to the function
@Preview
@Composable
fun DefaultPreview() {
    newUI()

}

@Preview
@Composable
fun DePreview() {
    HomeScreen()

}


