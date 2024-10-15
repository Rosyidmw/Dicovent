@file:Suppress("unused")

package com.rosyid.testnavdicovent.ui.detail

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rosyid.testnavdicovent.data.response.BaseResponse
import com.rosyid.testnavdicovent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {
    private val _eventDetail = MutableLiveData<BaseResponse>()
    val eventDetail: LiveData<BaseResponse> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    fun getDetailEvent(eventId: String) {
        _isLoading.value = true

        val apiService = ApiConfig.getApiService()
        apiService.getDetailEvent(eventId).enqueue(object : Callback<BaseResponse> {
            @SuppressLint("NullSafeMutableLiveData")
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()
                } else {
                    _eventDetail.value = null
                }
            }

            @SuppressLint("NullSafeMutableLiveData")
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                _isLoading.value = false
                _eventDetail.value = null
            }

        })
    }
}