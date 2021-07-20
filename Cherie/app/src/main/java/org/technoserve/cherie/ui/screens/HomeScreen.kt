package org.technoserve.cherie.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import org.technoserve.cherie.ui.navigation.NavigationItem

enum class HomeNavType { INFERENCE, LOGS }

@Composable
fun HomeScreen(navController: NavHostController) {
    val navItemState = rememberSaveable { mutableStateOf(HomeNavType.INFERENCE) }
    Scaffold(
        bottomBar = { HomeBottomNavigation(navItemState) },
        content = { HomeBodyContent(homeNavType = navItemState.value, navController = navController) }
    )
}

@Composable
fun HomeBodyContent(homeNavType: HomeNavType, navController: NavHostController) {
    Crossfade(
        targetState = homeNavType,
        animationSpec = tween(240)
    ) { navType ->
        when (navType) {
            HomeNavType.INFERENCE -> InferenceScreen(navController)
            HomeNavType.LOGS -> SavedPredictionsScreen(navController)
        }
    }
}


@Composable
fun HomeBottomNavigation(homeNavItemState: MutableState<HomeNavType>) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onSurface,
    ) {
        BottomNavigationItem(
            icon = { Icon(painterResource(id = NavigationItem.Inference.icon), contentDescription = NavigationItem.Inference.title) },
            label = { Text(text = NavigationItem.Inference.title) },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White.copy(0.4f),
            alwaysShowLabel = true,
            selected = homeNavItemState.value == HomeNavType.INFERENCE,
            onClick = { homeNavItemState.value = HomeNavType.INFERENCE },
        )
        BottomNavigationItem(
            icon = { Icon(painterResource(id = NavigationItem.Logs.icon), contentDescription = NavigationItem.Logs.title) },
            label = { Text(text = NavigationItem.Logs.title) },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White.copy(0.4f),
            alwaysShowLabel = true,
            selected = homeNavItemState.value == HomeNavType.LOGS,
            onClick = { homeNavItemState.value = HomeNavType.LOGS },
        )
    }
}