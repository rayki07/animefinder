package com.example.animefinder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animefinder.api.JikanApiService
import com.example.animefinder.api.JikanResponse
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AnimeViewModel : ViewModel() {

    // LiveData untuk menampung daftar Anime yang akan diobservasi oleh Activity
    private val _trendingAnime = MutableLiveData<List<Anime>>()
    val trendingAnime: LiveData<List<Anime>> = _trendingAnime

    // LiveData untuk menangani status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData untuk menangani pesan error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    //Data Paging
    private var currentPage = 1 //<-- Lacak halaman saat ini
    private var isLastPage = true   // <-- Tandai jika sudah mencapai halaman terakhir (diganti agar tidak crash)

    private val BASE_URL = "https://api.jikan.moe/v4/"

    // Inisialisasi API Service
    private val jikanApiService: JikanApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JikanApiService::class.java)
    }

    init {
        // Panggil saat ViewModel pertama kali dibuat
        fetchTrendingAnime()
    }

    fun fetchTrendingAnime() {
        // Jalankan Coroutine dalam scope ViewModel
        viewModelScope.launch {
            _isLoading.value = true // Mulai loading
            _errorMessage.value = null // Reset error
            try {
                // Panggil API service
                val response: JikanResponse = jikanApiService.getAnimeByPage(1) // Ambil halaman 1()

                // Konversi dan update LiveData
                val animeList: List<Anime> = response.data.map { it.toAppModel() }
                _trendingAnime.value = animeList

            } catch (e: Exception) {
                // Tangani error
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            } finally {
                _isLoading.value = false // Selesai loading
            }
        }
    }

    // Ubah nama fungsi menjadi loadNextPage untuk lebih jelas
    fun loadNextPage() {
        if (_isLoading.value == true || isLastPage) {
            return // Jangan muat jika sedang loading atau sudah halaman terakhir
        }

        // Cek apakah ini pemuatan data awal (halaman 1)
        if (currentPage == 1) {
            _trendingAnime.value = emptyList() // Kosongkan daftar saat pemuatan awal
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // Panggil API dengan parameter 'page' (Jikan API V4 menggunakan query 'page')
                // CATATAN: Endpoint Jikan top/anime tidak menerima query 'page',
                //          tapi kita asumsikan ada atau kita gunakan endpoint lain
                //          yang mendukung paging. Jika menggunakan top/anime,
                //          paging harus dilakukan secara bertahap atau beralih ke
                //          endpoint search yang lebih fleksibel.
                //
                // KARENA 'top/anime' TIDAK MENDUKUNG 'page', kita gunakan ENDPOINT
                // YANG LEBIH FLEKSIBEL seperti search: /v4/anime

                // Gunakan endpoint search yang defaultnya adalah trending/populer dan mendukung paging
                val response: JikanResponse = jikanApiService.getAnimeByPage(currentPage)

                val newAnimeList = response.data.map { it.toAppModel() }

                // Cek apakah ini halaman terakhir (asumsi JikanResponse punya data paging)
                // Di Jikan API V4, respons memiliki meta data 'pagination'
                isLastPage = response.pagination.has_next_page == false

                // Gabungkan data lama dengan data baru
                val currentList = _trendingAnime.value.orEmpty().toMutableList()
                currentList.addAll(newAnimeList)

                _trendingAnime.value = currentList

                currentPage++ // Naikkan nomor halaman

            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat halaman $currentPage: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }

    }
}