// Anime.kt

package com.example.animefinder // Sesuaikan dengan package Anda

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//Pastikan Anda menambahkan plugin parcelize di gradle(Module:App)
@Parcelize
data class Anime(
    val title: String,
    val imageUrl: String, // Nanti untuk library image loading (misal Glide/Coil)
    val rating: Double,
    val description: String, // Tambahkan detail deskripsi anime
    val genre: List<String>, // Tambahkan genre anime
) : Parcelable // <-- Implementasi Parcelable
