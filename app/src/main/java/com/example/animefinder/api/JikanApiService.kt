package com.example.animefinder.api

import retrofit2.http.GET
import retrofit2.http.Query // Import Query jika diperlukan

interface JikanApiService {

    // Endpoint untuk mendapatkan daftar anime terpopuler/trending
    // Dokumentasi Jikan V4: https://api.jikan.moe/v4/top/anime
    // Gunakan endpoint anime yang lebih fleksibel dan mendukung paging dengan parameter 'page'
    @GET("top/anime")
    suspend fun getAnimeByPage(@Query("page") page: Int): JikanResponse
    @GET("anime")
    suspend fun searchAnime(@Query("q") query: String): JikanResponse

    // CATATAN: endpoint /v4/anime defaultnya adalah search (populer)
}