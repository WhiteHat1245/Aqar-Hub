package com.example.aqarhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aqarhub.data.remote.BookingsApiService
import com.example.aqarhub.data.model.BookingResponse
import com.example.aqarhub.data.model.CreateBookingRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BookingsListState {
    object Loading : BookingsListState
    data class Success(val bookings: List<BookingResponse>) : BookingsListState
    data class Error(val message: String) : BookingsListState
}

sealed interface CreateBookingState {
    object Idle : CreateBookingState
    object Loading : CreateBookingState
    object Success : CreateBookingState
    data class Error(val message: String) : CreateBookingState
}

@HiltViewModel
class BookingsViewModel @Inject constructor(
    private val bookingsApi: BookingsApiService
) : ViewModel() {

    private val _listState = MutableStateFlow<BookingsListState>(BookingsListState.Loading)
    val listState: StateFlow<BookingsListState> = _listState

    private val _createState = MutableStateFlow<CreateBookingState>(CreateBookingState.Idle)
    val createState: StateFlow<CreateBookingState> = _createState

    fun loadUserBookings() {
        viewModelScope.launch {
            _listState.value = BookingsListState.Loading
            try {
                val bookings = bookingsApi.getUserBookings()
                _listState.value = BookingsListState.Success(bookings)
            } catch (e: Exception) {
                _listState.value = BookingsListState.Error(e.localizedMessage ?: "فشل تحميل الحجوزات")
            }
        }
    }

    fun createBooking(apartmentId: Int, startDate: String, endDate: String) {
        viewModelScope.launch {
            _createState.value = CreateBookingState.Loading
            try {
                val response = bookingsApi.createBooking(CreateBookingRequest(apartmentId, startDate, endDate))
                if (response.isSuccessful) {
                    _createState.value = CreateBookingState.Success
                } else {
                    _createState.value = CreateBookingState.Error(response.errorBody()?.string() ?: "فشل إنشاء الحجز")
                }
            } catch (e: Exception) {
                _createState.value = CreateBookingState.Error(e.localizedMessage ?: "حدث خطأ ما")
            }
        }
    }

    fun resetCreateState() {
        _createState.value = CreateBookingState.Idle
    }
}
