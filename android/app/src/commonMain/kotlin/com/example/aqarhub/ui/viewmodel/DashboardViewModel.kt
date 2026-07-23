package com.example.aqarhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aqarhub.data.local.AuthPreferences
import com.example.aqarhub.data.model.ApartmentResponse
import com.example.aqarhub.data.remote.ApartmentsApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val apartments: List<ApartmentResponse>) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val apartmentsApi: ApartmentsApiService,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        fetchApartments()
    }

    fun fetchApartments(region: String? = null, maxPrice: Double? = null) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                val list = apartmentsApi.getApartments(region, maxPrice)
                _uiState.value = DashboardUiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(
                    e.localizedMessage ?: "حدث خطأ أثناء تحميل بيانات العقارات"
                )
            }
        }
    }

    fun logout() {
        authPreferences.clearToken()
    }
}
