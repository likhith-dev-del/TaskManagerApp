package com.likhith.taskmanagerapp

import retrofit2.http.GET

interface ApiService {

    @GET("todos")   // ✅ ONLY endpoint
    suspend fun getTasks(): List<ApiTask>
}