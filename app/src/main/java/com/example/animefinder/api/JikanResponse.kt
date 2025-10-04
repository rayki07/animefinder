package com.example.animefinder.api

// Data class utama yang menampung daftar anime (sesuai root JSON "data")
data class JikanResponse(
    val data: List<AnimeApi>, // Ganti nama AnimeApi agar tidak bentrok dengan Anime.kt lama
    val pagination: Pagination // Tambahkan pagination jika diperlukan
)

// Model untuk pagination
data class Pagination(
    val last_visible_page: Int,
    val has_next_page: Boolean, // <-- Ini yang kita butuhkan untuk is LastPage
    val current_page: Int
)

// Representasi satu objek Anime dari API
data class AnimeApi(
    val mal_id: Int,
    val title: String,
    val images: Images,
    val score: Double?, // score bisa null
    val synopsis: String?, // synopsis bisa null
    val genres: List<Genre>
) {
    // Ubah AnimeApi menjadi Anime model kita
    fun toAppModel(): com.example.animefinder.Anime {
        val imageUrl = images.jpg.image_url // Ambil URL gambar JPG
        val rating = score ?: 0.0 // Jika score null, set 0.0
        val description = synopsis ?: "Sinopsis belum tersedia."
        val genreNames = genres.map { it.name }

        return com.example.animefinder.Anime(
            title = title,
            imageUrl = imageUrl,
            rating = rating,
            description = description,
            genre = genreNames
        )
    }
}

data class Images(
    val jpg: Jpg
)

data class Jpg(
    val image_url: String
)

data class Genre(
    val name: String
)