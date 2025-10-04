// AnimeAdapter.kt

package com.example.animefinder // Sesuaikan dengan package Anda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AnimeAdapter(
    // Ubah animeList menjadi 'var' agar bisa diupdate
    private var animeList: List<Anime>,
    private val listener: OnItemClickListener // Tambahkan listener
) :
    RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    // Tambahkan interface untuk listener
        interface OnItemClickListener {
        fun onItemClick(anime: Anime)
    }


    // 1. ViewHolder: Menampung view untuk satu item
    inner class AnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title: TextView = itemView.findViewById(R.id.tv_anime_title)
        val rating: TextView = itemView.findViewById(R.id.tv_anime_rating)
        val poster: ImageView = itemView.findViewById(R.id.iv_anime_poster)
        // Jika menggunakan library image loading, kode akan ada di onBindViewHolder

        init {
            itemView.setOnClickListener(this) //Set listener pada seluruh item view
        }


        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val anime = animeList[position]
                listener.onItemClick(animeList[position]) // Panggil listener di Activity
            }
        }
    }

    // 2. Membuat ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime_card, parent, false)
        return AnimeViewHolder(view)
    }

    // FUNGSI BARU: Untuk memperbarui data dari ViewModel
    fun updateData(newAnimeList: List<Anime>) {
        this.animeList = newAnimeList
        notifyDataSetChanged() // Cara sederhana, bisa dioptimalkan dengan DiffUtil
    }


    // 3. Mengaitkan data ke ViewHolder
    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.title.text = anime.title
        holder.rating.text = "⭐ ${anime.rating}"
        //holder.poster.setImageResource(...) // untuk gambar

        // --- Contoh sederhana untuk gambar (di dunia nyata, gunakan Glide/Coil) ---
        // Untuk saat ini, kita hanya menampilkan placeholder/background yang sudah ada di XML
        // holder.poster.setImageResource(R.drawable.placeholder_anime) // Misal Anda punya gambar
        // --------------------------------------------------------------------------

        // Menggunakan Glide untuk memuat gambar dari URL
        // Di aplikasi nyata, anime.imageUrl akan berisi URL gambar
        Glide.with(holder.itemView.context)
            // Kita menggunakan placeholder dari internet, ganti dengan URL sungguhan
            // Karena data dummy, kita bisa menggunakan placeholder umum:
            .load(anime.imageUrl)
            .placeholder(R.drawable.ic_search)
            .into(holder.poster)

    }

    // 4. Mengembalikan jumlah item
    override fun getItemCount() = animeList.size
}