package org.technoserve.cherie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ButtonPrimary(
    onClick: () -> Unit,
    label: String,
    requiredWidth: Dp = 160.dp,
    enabled: Boolean = true,
    content: @Composable (() -> Unit)? = null
){
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier.requiredWidth(requiredWidth),
        shape = RoundedCornerShape(0),
        enabled = enabled,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        if(content == null){
            Text(
                text = label,
                modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        } else {
            content()
        }
    }
}

@Composable
fun ButtonSecondary(
    onClick: () -> Unit,
    label: String,
    requiredWidth: Dp = 160.dp,
    enabled: Boolean = true,
    content: @Composable (() -> Unit)? = null
){
    Button(
        onClick = { onClick() },
        enabled =  enabled,
        modifier = Modifier
            .requiredWidth(requiredWidth)
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
        if(content == null){
            Text(
                text = label,
                modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
        } else {
            content()
        }
    }
}