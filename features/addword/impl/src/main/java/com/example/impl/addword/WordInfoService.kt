package com.example.impl.addword

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class WordInfoService {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    // http://10.0.2.2:8080/api/word_info
    private val baseUrl = " http://10.0.2.2:8080/api/word_info"
    
    suspend fun getWordInfo(query: String): Result<WordInfoResponse> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = "$baseUrl?query=\"$encodedQuery\""
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("Unexpected code: ${response.code}")
                )
            }
            
            val responseBody = response.body?.string()
            if (responseBody == null) {
                return@withContext Result.failure(
                    IOException("Response body is null")
                )
            }
            
            val wordInfo = json.decodeFromString<WordInfoResponse>(responseBody)
            Result.success(wordInfo)
        } catch (e: Exception) {
            Log.e("WordInfoService", "Error fetching word info", e)
            Result.failure(e)
        }
    }
}

