package com.example.applicationscanning.coment

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("predict")
    suspend fun predict(@Body request: PredictRequest): PredictResponse
}
