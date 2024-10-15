package com.rosyid.testnavdicovent.data.retrofit

import com.rosyid.testnavdicovent.data.response.BaseResponse
import com.rosyid.testnavdicovent.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active : Int
    ): Call<EventResponse>

    @GET("events")
    fun searchEvents(@Query("active") active: Int, @Query("q") keyword: String): Call<BaseResponse>

    @GET("events/{id}")
    fun getDetailEvent(@Path("id") id: String): Call<BaseResponse>

}