@file:Suppress("unused")

package com.rosyid.testnavdicovent.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rosyid.testnavdicovent.data.response.EventResponse
import com.rosyid.testnavdicovent.data.response.ListEventsItem
import com.rosyid.testnavdicovent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadUpcomingEvents() {
        _isLoading.value = true
        val apiService = ApiConfig.getApiService()
        apiService.getEvents(1).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    _upcomingEvents.value = events
                } else {
                    _upcomingEvents.value = emptyList()
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _upcomingEvents.value = emptyList()
            }
        })
    }

    fun loadFinishedEvents() {
        _isLoading.value = true
        val apiService = ApiConfig.getApiService()
        apiService.getEvents(0).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    _finishedEvents.value = events
                } else {
                    _finishedEvents.value = emptyList()
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _finishedEvents.value = emptyList()
            }
        })
    }
}
