package com.example.impl.addword

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class WordInfoService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    private val json = Json { ignoreUnknownKeys = true }

    private val baseUrl = "http://192.144.15.171/api/word_info"
    
    suspend fun getWordInfo(query: String): Result<WordInfoResponse> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = "$baseUrl?query=$encodedQuery"
            Log.d("WordInfoService", "Fetching word info from URL: $url")
            
            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", "EnglishApp/1.0")
                .addHeader("Accept", "application/json")
                .build()
            
            Log.d("WordInfoService", "Executing request...")
            val response = client.newCall(request).execute()
            Log.d("WordInfoService", "Response code: ${response.code}")
            
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                Log.e("WordInfoService", "Request failed with code ${response.code}, body: $errorBody")
                return@withContext Result.failure(
                    IOException("Unexpected code: ${response.code}, body: $errorBody")
                )
            }
            
            val responseBody = response.body?.string()
            Log.d("WordInfoService", "Response body length: ${responseBody?.length ?: 0}")
            if (responseBody == null) {
                Log.e("WordInfoService", "Response body is null")
                return@withContext Result.failure(
                    IOException("Response body is null")
                )
            }
            
            Log.d("WordInfoService", "Response body: $responseBody")
            try {
                val wordInfo = json.decodeFromString<WordInfoResponse>(responseBody)
                val cleanedWordInfo = wordInfo.copy(
                    title = wordInfo.title.trim('"'),
                    translation = wordInfo.translation.trim('"'),
                    title_pronunciation = wordInfo.title_pronunciation.trim('"'),
                    translation_pronunciation = wordInfo.translation_pronunciation.trim('"'),
                    img = wordInfo.img.trim('"'),
                    example = wordInfo.example?.trim('"')
                )
                Log.d("WordInfoService", "Successfully parsed word info: $cleanedWordInfo")
                Result.success(cleanedWordInfo)
            } catch (e: Exception) {
                Log.e("WordInfoService", "Failed to parse JSON response", e)
                Log.e("WordInfoService", "Response body that failed to parse: $responseBody")
                Result.failure(IOException("Failed to parse response: ${e.message}", e))
            }
        } catch (e: Exception) {
            Log.e("WordInfoService", "Error fetching word info", e)
            Log.e("WordInfoService", "Exception type: ${e.javaClass.simpleName}, message: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

