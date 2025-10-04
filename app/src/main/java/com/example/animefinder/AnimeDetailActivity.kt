// AnimeDetailActivity.kt

package com.example.animefinder

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide

class AnimeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar_detail)
        setSupportActionBar(toolbar)
        // Aktifkan tombol kembali (Up button)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Judul akan diset di TV

        // Ambil objek Anime yang dikirim dari MainActivity
        val anime = getParcelableExtraAnime()

        if (anime != null) {
            displayAnimeDetails(anime)
        } else {
            // Handle jika data tidak ada (misalnya tampilkan error atau tutup activity)
            finish()
        }
    }

    // Fungsi untuk mendapatkan objek Parcelable
    private fun getParcelableExtraAnime(): Anime? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Menggunakan method yang lebih baru untuk SDK 33+
            intent.getParcelableExtra("ANIME_DATA", Anime::class.java)
        } else {
            // Menggunakan method deprecated untuk SDK <= 32 (SDK 24 masuk sini)
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("ANIME_DATA")
        }
    }

    private fun displayAnimeDetails(anime: Anime) {
        val tvTitle: TextView = findViewById(R.id.tv_detail_title)
        val tvRating: TextView = findViewById(R.id.tv_detail_rating)
        val tvGenre: TextView = findViewById(R.id.tv_detail_genre)
        val tvDescription: TextView = findViewById(R.id.tv_detail_description)
        val ivPoster: ImageView = findViewById(R.id.iv_detail_poster)

        tvTitle.text = anime.title
        tvRating.text = "⭐ ${anime.rating}"
        tvGenre.text = anime.genre.joinToString(", ")
        tvDescription.text = anime.description

        // Memuat gambar menggunakan Glide
        Glide.with(this)
            .load(anime.imageUrl)
            .placeholder(R.drawable.ic_search)
            .error(R.drawable.ic_search)
            .into(ivPoster)

        // Set judul toolbar (opsional)
        supportActionBar?.title = anime.title
    }

    // Menangani tombol kembali (Up button)
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}