@file:Suppress("unused")

package com.rosyid.testnavdicovent.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rosyid.testnavdicovent.data.response.EventResponse
import com.rosyid.testnavdicovent.data.response.ListEventsItem
import com.rosyid.testnavdicovent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedViewModel : ViewModel() {

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getFinishedEvents() {
        _isLoading.value = true
        val apiService = ApiConfig.getApiService()

        apiService.getEvents(0).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    _finishedEvents.value = events
                } else {
                    _errorMessage.value = response.message()
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message
            }
        })
    }

    fun filterEvents(query: String): List<ListEventsItem> {
        return _finishedEvents.value?.filter { event ->
            event.name?.contains(query, ignoreCase = true) == true
        } ?: emptyList()
    }
}
