package com.example.animefinder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Implementasikan AnimeAdapter.OnItemClickListener agar hasil search bisa diklik
class SearchActivity : AppCompatActivity(), AnimeAdapter.OnItemClickListener {

    private lateinit var searchView: SearchView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoResults: TextView

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Sembunyikan judul bawaan

        // Inisialisasi Views
        searchView = findViewById(R.id.search_view)
        rvSearchResults = findViewById(R.id.rv_search_results)
        progressBar = findViewById(R.id.progress_bar_search)
        tvNoResults = findViewById(R.id.tv_no_results)

        // Setup RecyclerView
        setupRecyclerView()

        // Setup Search Listener
        setupSearchListener()

        // Observasi Data
        observeViewModel()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        // Gunakan AnimeAdapter yang sama
        rvSearchResults.layoutManager = GridLayoutManager(this, 2)
        // Mulai dengan list kosong
        rvSearchResults.adapter = AnimeAdapter(emptyList(), this)
    }

    private fun setupSearchListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    viewModel.searchAnime(query)
                }
                searchView.clearFocus() // Tutup keyboard
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Panggil pencarian setiap kali teks berubah (dengan debounce 500ms di ViewModel)
                viewModel.searchAnime(newText.orEmpty())
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { results ->
            // Update Adapter
            (rvSearchResults.adapter as AnimeAdapter).updateData(results)

            // Atur visibility
            val isQueryEmpty = viewModel.isSearchQueryEmpty.value ?: true
            if (!isQueryEmpty) {
                tvNoResults.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
            } else {
                // Tampilkan pesan awal (misalnya "Mulai ketik untuk mencari") jika perlu
                tvNoResults.text = "Silakan masukkan kata kunci pencarian."
                tvNoResults.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            rvSearchResults.visibility = if (isLoading) View.GONE else View.VISIBLE

            // Sembunyikan "No Results" saat loading
            if (isLoading) tvNoResults.visibility = View.GONE
        }

        viewModel.isSearchQueryEmpty.observe(this) { isEmpty ->
            if (isEmpty) {
                tvNoResults.text = "Silakan masukkan kata kunci pencarian."
                tvNoResults.visibility = View.VISIBLE
            }
        }
    }

    // Implementasi klik untuk hasil pencarian (Sama seperti di MainActivity)
    override fun onItemClick(anime: Anime) {
        val intent = Intent(this, AnimeDetailActivity::class.java)
        intent.putExtra("ANIME_DATA", anime)
        startActivity(intent)
    }
}