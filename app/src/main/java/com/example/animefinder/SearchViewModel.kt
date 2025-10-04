package com.example.animefinder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animefinder.api.JikanApiService
import com.example.animefinder.api.JikanResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<Anime>>()
    val searchResults: LiveData<List<Anime>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSearchQueryEmpty = MutableLiveData<Boolean>(true)
    val isSearchQueryEmpty: LiveData<Boolean> = _isSearchQueryEmpty

    private val BASE_URL = "https://api.jikan.moe/v4/"

    private val jikanApiService: JikanApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JikanApiService::class.java)
    }

    private var searchJob: Job? = null // Untuk debouncing

    fun searchAnime(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isSearchQueryEmpty.value = true
            searchJob?.cancel() // Batalkan job jika query kosong
            return
        }

        _isSearchQueryEmpty.value = false
        _isLoading.value = true

        // Batalkan job sebelumnya (debounce)
        searchJob?.cancel()

        // Buat job baru dengan delay untuk menghindari spam API
        searchJob = viewModelScope.launch {
            delay(500) // Tunggu 500ms setelah user berhenti mengetik

            try {
                // Panggil endpoint search Jikan API
                val response: JikanResponse = jikanApiService.searchAnime(query)

                // Konversi dan posting hasilnya
                val animeList: List<Anime> = response.data.map { it.toAppModel() }
                _searchResults.postValue(animeList) // Gunakan postValue karena ini dari Coroutine

            } catch (e: Exception) {
                // Handle error (bisa menggunakan LiveData error seperti di MainActivity)
                _searchResults.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}