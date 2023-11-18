package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.data.ContactDao
import com.example.contacts.data.ContactRoomDatabase
import com.example.contacts.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    // DAO untuk mengakses database
    private lateinit var mContactDao: ContactDao
    // ExecutorService untuk menjalankan operasi database secara asinkron
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set tema gelap menjadi non-aktif
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Menginisialisasi executorService untuk menjalankan operasi database di thread terpisah
        executorService = Executors.newSingleThreadExecutor()
        // Mendapatkan instance database
        val db = ContactRoomDatabase.getDatabase(this)
        // Mendapatkan DAO untuk mengakses data kontak
        mContactDao = db!!.contactDao()!!

        with(binding) {
            btnAddContact.setOnClickListener {
                // Buka activity formulir tambah kontak baru
                startActivity(
                    Intent(this@MainActivity, ContactFormActivity::class.java)
                )
            }

            btnSearchContact.setOnClickListener {
                // Buka activity pencarian kontak
                startActivity(
                    Intent(this@MainActivity, SearchActivity::class.java)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Mendapatkan dan menampilkan semua kontak saat aktivitas di-resume
        getAllContacts()
    }

    private fun getAllContacts() {
        // Mengamati perubahan dalam data kontak
        mContactDao.allContacts.observe(this) { contacts ->
            // Buat adapter kontak dengan menggunakan data terbaru
            val adapterContact = ContactAdapter(contacts) { contact ->
                // Buka activity detail kontak saat kontak diklik
                startActivity(
                    Intent(this@MainActivity, ContactDetailActivity::class.java)
                        .putExtra("contact", contact)
                )
            }

            // Terapkan adapter ke RecyclerView
            binding.rvContact.apply {
                adapter = adapterContact
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }
}