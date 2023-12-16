package com.interexy.statustracker.doman.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.interexy.statustracker.data.StatusTrackerDataStore
import com.interexy.statustracker.presentation.ui.screens.Contacts.ContactsScreen
import com.interexy.statustracker.presentation.ui.screens.FeedbackScreen.FeedbackScreen
import com.interexy.statustracker.presentation.ui.screens.Home.HomeScreen
import com.interexy.statustracker.presentation.ui.screens.LaunchWhatsApp.LaunchWhatsAppScreen
import com.interexy.statustracker.presentation.ui.screens.PIN.PINScreen
import com.interexy.statustracker.presentation.ui.screens.ResultScreen.ResultScreen
import com.interexy.statustracker.presentation.ui.screens.Settings.SettingsScreen
import com.interexy.statustracker.presentation.ui.screens.Splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavHostGraph(
    navController: NavHostController,
    isDark:Boolean
) {
    val context = LocalContext.current


    NavHost(
        navController = navController,
        startDestination = NavigationScreen.Splash.route
    ) {
        composable(route = NavigationScreen.PIN.route) {
            PINScreen(navController = navController)
        }
        composable(route = NavigationScreen.HomeScreen.route) {
            HomeScreen(
                onNavigate = {
                if (it.route == NavigationScreen.PIN.route){
                    navController.popBackStack()
                    navController.navigate(it.route)
                }else{
                    navController.navigate(it.route)
                }
            },
              isDark = isDark
                )
        }
        composable(route = NavigationScreen.Splash.route) {
            SplashScreen(navController)
        }
        composable(route = NavigationScreen.SettingsScreen.route) {
            SettingsScreen(isDark = isDark)
        }

        composable(route = NavigationScreen.ContactsScreen.route) {
            ContactsScreen(navController = navController, isDark = isDark)
        }
        composable(route = NavigationScreen.FeedbackScreen.route) {
            FeedbackScreen(navController = navController, isDark = isDark)
        }

        composable(route = NavigationScreen.LaunchWhatsApp.route) {
            LaunchWhatsAppScreen(onNavigate = {
                navController.popBackStack()
                navController.navigate(NavigationScreen.ResultScreen.route)
            })
        }

        composable(route = NavigationScreen.ResultScreen.route) {
            ResultScreen(OnNavigate = {route->
                navController.popBackStack()
                navController.navigate(route)
            })
        }
    }


}