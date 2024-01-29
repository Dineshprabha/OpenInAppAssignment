package com.dineshprabha.openinappassignment.api

import com.dineshprabha.openinappassignment.models.UserClickResponse
import retrofit2.Response
import retrofit2.http.GET

interface APIService {

    @GET("dashboardNew")
    suspend fun getDashboardData(): Response<UserClickResponse>

}