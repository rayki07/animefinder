// MainActivity.kt

package com.example.animefinder // Sesuaikan dengan package Anda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animefinder.api.JikanApiService
import com.example.animefinder.api.JikanResponse // Assuming you have this file
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
// import android.widget.ProgressBar // You can uncomment this if you add the ProgressBar view
import android.view.Menu // untuk menu
import android.view.MenuItem // untuk menu
import androidx.appcompat.app.AlertDialog // untuk dialog


class MainActivity : AppCompatActivity(), AnimeAdapter.OnItemClickListener {

    //Deklarasi Variabel
    private lateinit var rvTrendingAnime: RecyclerView
    private lateinit var cardSearch: MaterialCardView
    // private lateinit var progressBar: ProgressBar // Uncomment if you have a ProgressBar in your layout

    // Inisialisasi ViewModel
    private val viewModel: AnimeViewModel by lazy {
        ViewModelProvider(this)[AnimeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup Toolbar sebagai Header
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Inisialisasi Views
        rvTrendingAnime = findViewById(R.id.rv_trending_anime)
        cardSearch = findViewById(R.id.card_search)
        // progressBar = findViewById(R.id.progress_bar) // Uncomment this line

        // Setup Search Bar
        cardSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // Setup RecyclerView with an initial empty list
        setupRecyclerView(emptyList())

        // Start observing data from the ViewModel
        observeViewModel()

        // Fetch data from the API
        loadNextPage()

        addScrollListener()
    }

    // --- MOVE THE FOLLOWING FUNCTIONS OUTSIDE of onCreate ---

    private fun setupRecyclerView(animeList: List<Anime>) {
        // Check if adapter is already set, if so, update data. Otherwise, create new.
        if (rvTrendingAnime.adapter == null) {
            val adapter = AnimeAdapter(animeList, this)
            val layoutManager = GridLayoutManager(this, 2)
            rvTrendingAnime.layoutManager = layoutManager
            rvTrendingAnime.adapter = adapter
        } else {
            (rvTrendingAnime.adapter as AnimeAdapter).updateData(animeList)
        }
    }

    private fun observeViewModel() {
        // 1. Observe the list of Anime
        viewModel.trendingAnime.observe(this) { animeList ->
            // When data changes, update the RecyclerView adapter
            setupRecyclerView(animeList) // Use setupRecyclerView to handle the update
        }

        // 2. Observe the Loading status
        viewModel.isLoading.observe(this) { isLoading ->
            // TODO: Implement loading visualization logic here
            // if (isLoading) {
            //     progressBar.visibility = View.VISIBLE
            // } else {
            //     progressBar.visibility = View.GONE
            // }
        }

        // 3. Observe Error messages
        viewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                // You might want to add logic in your ViewModel to handle "one-shot" events
            }
        }
    }

    private fun loadNextPage() {
        // The logic to fetch data should be inside the ViewModel, not the Activity.
        // For now, let's trigger the fetch from the ViewModel.
        viewModel.fetchTrendingAnime()
    }

    // This function is now handled by the ViewModel
    /*
    private fun fetchTrendingAnime() {
        lifecycleScope.launch {
            try {
                val response: JikanResponse = apiService.getTopAnime()
                val animeList: List<Anime> = response.data.map { it.toAppModel() }
                setupRecyclerView(animeList)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Gagal mengambil data dari Jikan API", e)
                Toast.makeText(this@MainActivity, "Gagal memuat anime: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    */

    // FUNGSI BARU: Untuk menampilkan menu di Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // FUNGSI BARU: Untuk menangani klik pada item menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_help -> {
                showHelpDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // FUNGSI BARU: Untuk menampilkan dialog ucapan terima kasih
    private fun showHelpDialog() {
        // Data Tim
        val teamInfo = """
            Tim Pengembang:
            Firman Wahyudi (2255201297)
            M. Dedi Muchsoleh (2255201317)
            Anang sukana (2255201085)
            Andrian nabil muzaki (2255201102)

        """.trimIndent()

        // Konten Dialog
        val message = """
            Aplikasi ini berhasil dibuat berkat bantuan dari Google Gemini. 
            Terima kasih banyak atas petunjuk dan kode Kotlin yang telah diberikan!
            
            $teamInfo
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Tentang Aplikasi AnimeFinder")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Function to handle item clicks
    override fun onItemClick(anime: Anime) {
        val intent = Intent(this, AnimeDetailActivity::class.java)
        intent.putExtra("ANIME_DATA", anime) // Send the Parcelable Anime object
        startActivity(intent)
    }

    private fun addScrollListener() {
        rvTrendingAnime.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager

                // Item terakhir yang terlihat
                val visibleItemCount = layoutManager.childCount
                // Jumlah item total dalam adapter
                val totalItemCount = layoutManager.itemCount
                // Posisi item pertama yang terlihat
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Deteksi ketika hampir mencapai akhir (misalnya 4 item sebelum akhir)
                val shouldLoadMore =
                    (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4
                            && firstVisibleItemPosition >= 0 // Pastikan bukan awal list
                            && totalItemCount >= 20 // Pastikan sudah ada cukup data (opsional)

                if (shouldLoadMore) {
                    viewModel.loadNextPage() // Muat halaman berikutnya
                }
            }
        })
    }
}

