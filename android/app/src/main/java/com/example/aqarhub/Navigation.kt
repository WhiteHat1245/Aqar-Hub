package com.example.aqarhub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.aqarhub.data.local.EncryptedSharedPreferencesManager
import com.example.aqarhub.ui.screens.ApartmentDetailScreen
import com.example.aqarhub.ui.screens.ApartmentsListScreen
import com.example.aqarhub.ui.screens.BookingsListScreen
import com.example.aqarhub.ui.screens.LoginScreen

@Composable
fun MainNavigation(encryptedPrefs: EncryptedSharedPreferencesManager) {
  val hasToken = remember { !encryptedPrefs.getAccessToken().isNullOrBlank() }
  val startDestination = if (hasToken) ApartmentsList else Login
  val backStack = rememberNavBackStack(startDestination)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Login> {
          LoginScreen(
            onLoginSuccess = {
              backStack.removeLastOrNull()
              backStack.add(ApartmentsList)
            }
          )
        }
        entry<ApartmentsList> {
          ApartmentsListScreen(
            onApartmentClick = { id ->
              backStack.add(ApartmentDetail(id))
            },
            onNavigateToBookings = {
              backStack.add(BookingsList)
            },
            onLogout = {
              encryptedPrefs.deleteAccessToken()
              backStack.removeLastOrNull()
              backStack.add(Login)
            }
          )
        }
        entry<ApartmentDetail> { key ->
          ApartmentDetailScreen(
            apartmentId = key.id,
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<BookingsList> {
          BookingsListScreen(
            onBack = { backStack.removeLastOrNull() }
          )
        }
      },
  )
}
