package com.example.contacts

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.data.ContactDao
import com.example.contacts.data.ContactRoomDatabase
import com.example.contacts.databinding.ActivitySearchBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SearchActivity : AppCompatActivity() {
    val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }
    private lateinit var mContactDao: ContactDao
    private lateinit var executorService: ExecutorService
    private lateinit var adapterContact: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = ContactRoomDatabase.getDatabase(this)
        mContactDao = db!!.contactDao()!!

        // Menginisialisasi adapterContact dengan daftar kontak kosong
        adapterContact = ContactAdapter(emptyList()) { contact ->
            // Menavigasi ke DetailContactActivity ketika item kontak diklik
            startActivity(
                Intent(this@SearchActivity, ContactDetailActivity::class.java)
                    .putExtra("contact", contact)
            )
            finish()
        }

        with(binding) {
            // Mengatur RecyclerView dengan adapterContact dan layout manager
            rvContact.apply {
                adapter = adapterContact
                layoutManager = LinearLayoutManager(this@SearchActivity)
            }

            // Menambahkan TextWatcher ke EditText untuk mendeteksi perubahan teks saat melakukan pencarian
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // Ketika teks berubah, panggil metode untuk memperbarui hasil pencarian
                    updateSearchResults(s.toString())
                }
            })

            // Meminta fokus pada EditText agar keyboard muncul otomatis
            edtSearch.requestFocus()

            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    // Metode untuk memperbarui hasil pencarian
    private fun updateSearchResults(searchQuery: String) {
        executorService.execute {
            val searchResults = if (searchQuery.isNotEmpty()) {
                // Lakukan pencarian berdasarkan query jika tidak kosong
                mContactDao.searchContacts("%$searchQuery%")
            } else {
                // Jika teks pencarian kosong, hasil pencarian juga kosong
                emptyList()
            }

            runOnUiThread {
                // Update RecyclerView dengan hasil pencarian
                val listSize = adapterContact.updateData(searchResults)
                // Atur visibilitas RecyclerView berdasarkan apakah ada hasil pencarian atau tidak
                binding.rvContact.visibility = if (listSize > 0) View.VISIBLE else View.GONE
            }
        }
    }
}