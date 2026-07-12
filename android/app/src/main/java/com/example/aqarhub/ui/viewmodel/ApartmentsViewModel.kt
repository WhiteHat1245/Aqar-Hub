package com.example.aqarhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aqarhub.data.remote.ApartmentsApiService
import com.example.aqarhub.data.model.ApartmentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ApartmentsState {
    object Loading : ApartmentsState
    data class Success(val apartments: List<ApartmentResponse>) : ApartmentsState
    data class Error(val message: String) : ApartmentsState
}

sealed interface ApartmentDetailState {
    object Loading : ApartmentDetailState
    data class Success(val apartment: ApartmentResponse) : ApartmentDetailState
    data class Error(val message: String) : ApartmentDetailState
}

@HiltViewModel
class ApartmentsViewModel @Inject constructor(
    private val apiService: ApartmentsApiService
) : ViewModel() {

    private val _listState = MutableStateFlow<ApartmentsState>(ApartmentsState.Loading)
    val listState: StateFlow<ApartmentsState> = _listState

    private val _detailState = MutableStateFlow<ApartmentDetailState>(ApartmentDetailState.Loading)
    val detailState: StateFlow<ApartmentDetailState> = _detailState

    fun loadApartments(region: String? = null, maxPrice: Double? = null) {
        viewModelScope.launch {
            _listState.value = ApartmentsState.Loading
            try {
                val regionParam = region?.ifBlank { null }
                val apartments = apiService.getApartments(regionParam, maxPrice)
                _listState.value = ApartmentsState.Success(apartments)
            } catch (e: Exception) {
                _listState.value = ApartmentsState.Error(e.localizedMessage ?: "فشل تحميل العقارات")
            }
        }
    }

    fun loadApartmentDetail(id: Int) {
        viewModelScope.launch {
            _detailState.value = ApartmentDetailState.Loading
            try {
                val apartment = apiService.getApartmentById(id)
                _detailState.value = ApartmentDetailState.Success(apartment)
            } catch (e: Exception) {
                _detailState.value = ApartmentDetailState.Error(e.localizedMessage ?: "فشل تحميل تفاصيل العقار")
            }
        }
    }
}
