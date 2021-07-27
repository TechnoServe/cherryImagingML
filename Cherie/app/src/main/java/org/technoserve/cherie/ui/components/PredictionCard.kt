package org.technoserve.cherie.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.joda.time.DateTime
import org.technoserve.cherie.database.Prediction
import org.technoserve.cherie.database.PredictionViewModel
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.technoserve.cherie.SavedPredictionActivity

var fmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

@Composable
fun PredictionCard(
    prediction: Prediction,
    predictionViewModel: PredictionViewModel
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.height(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = DateTime(prediction.createdAt).toString(fmt),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = "Score: ${prediction.ripe}",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .align(Alignment.CenterVertically)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        onClick = {
                            val intent = SavedPredictionActivity.newIntent(context, prediction.id)
                            context.startActivity(intent)
                        },
                    )
            ) {
                Image(
                    bitmap = prediction.inputImage.asImageBitmap(),
                    modifier = Modifier
                        .width(120.dp)
                        .height(120.dp),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    bitmap = prediction.mask.asImageBitmap(),
                    modifier = Modifier
                        .width(120.dp)
                        .height(120.dp),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .requiredWidth(120.dp)
                        .background(MaterialTheme.colors.background)
                        .border(1.dp, MaterialTheme.colors.primary),
                    shape = RoundedCornerShape(0),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "Sync",
                        modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { predictionViewModel.deletePrediction(prediction) },
                    modifier = Modifier.requiredWidth(120.dp),
                    shape = RoundedCornerShape(0),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "Delete",
                        modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}