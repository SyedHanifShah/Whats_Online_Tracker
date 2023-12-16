package com.interexy.statustracker.presentation.ui.screens.MainScreen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.interexy.statustracker.doman.util.MFontFamily
import com.interexy.statustracker.doman.util.NavHostGraph
import com.interexy.statustracker.doman.util.NavigationScreen
import com.interexy.statustracker.presentation.theme.SilverGray
import com.interexy.statustracker.presentation.theme.WhatsAppColor
import com.interexy.statustracker.presentation.theme.WhatsAppDarkColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(isDark:Boolean) {
    val navController = rememberNavController()
    val backStackEntry by
    navController.currentBackStackEntryAsState()

    var showBar by remember {
        mutableStateOf(true)
    }
    showBar = when (backStackEntry?.destination?.route) {
        NavigationScreen.PIN.route, NavigationScreen.LaunchWhatsApp.route, NavigationScreen.PIN.route, NavigationScreen.Splash.route, NavigationScreen.ContactsScreen.route, NavigationScreen.ResultScreen.route -> false
        else -> true
    }

    Scaffold(
        bottomBar = { if (showBar) BottomBar(navController = navController, isDark = isDark) },
        modifier = Modifier.navigationBarsPadding().statusBarsPadding()
    ) {
        Box(modifier = Modifier.padding(it))
        NavHostGraph(navController = navController, isDark = isDark)
    }
}


@Composable
fun BottomBar(
    navController: NavHostController, isDark: Boolean
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isDarkTheme = isSystemInDarkTheme()

    val screens: List<NavigationScreen> = listOf(
        NavigationScreen.HomeScreen,
        NavigationScreen.FeedbackScreen,
        NavigationScreen.SettingsScreen
    )






    BottomAppBar(
        containerColor = if (isDark || isDarkTheme) WhatsAppDarkColor else WhatsAppColor,
        modifier = Modifier.height(70.dp)
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController,
                isDark = isDark
            )
        }

    }
}


@Composable
fun RowScope.AddItem(
    screen: NavigationScreen,
    currentDestination: NavDestination?,
    navController: NavHostController,
    isDark: Boolean
) {
    val currentRoute = currentDestination?.route?.substringBefore("/")
    val isDarkTheme = isSystemInDarkTheme()
    val mFontFamily = MFontFamily()

    NavigationBarItem(
        label = {
            Text(
                text = screen.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = mFontFamily.RobotoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp
                )
            )
        },
        icon = {
            Icon(
                painter = painterResource(id = screen.icon),
                contentDescription = "Navigation Icon",
                modifier = Modifier.size(if (screen.route == NavigationScreen.FeedbackScreen.route) 25.dp else 20.dp)
            )
        },

        selected = currentDestination?.hierarchy?.any {
            currentRoute == screen.route
        } == true,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = if (isDark || isDarkTheme) WhatsAppColor else Color.White,
            unselectedIconColor = if (isDark || isDarkTheme) SilverGray else WhatsAppDarkColor,
            selectedTextColor = if (isDark || isDarkTheme) WhatsAppColor else Color.White,
            unselectedTextColor =if (isDark || isDarkTheme) SilverGray  else WhatsAppDarkColor,
            indicatorColor =if (isDark || isDarkTheme) WhatsAppDarkColor else WhatsAppColor
        ),
        onClick = {
            navController.navigate(screen.route){
                popUpTo(NavigationScreen.HomeScreen.route)
                launchSingleTop = true
            }

        }
    )

}